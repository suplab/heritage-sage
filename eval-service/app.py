from fastapi import FastAPI, File, UploadFile, Form
from fastapi.responses import JSONResponse
from pydantic import BaseModel
import uvicorn
import shutil, os, uuid
import numpy as np
import cv2
from skimage.metrics import structural_similarity as ssim

app = FastAPI(title="heritage-sage-eval", version="0.2")

class EvalResponse(BaseModel):
    score: float
    feedback: str

def score_against_reference(upload_path, ref_path):
    imgA = cv2.imread(ref_path, cv2.IMREAD_GRAYSCALE)
    imgB = cv2.imread(upload_path, cv2.IMREAD_GRAYSCALE)
    if imgA is None or imgB is None:
        return 0.0, "Could not read images for comparison."

    h, w = imgA.shape
    imgB_resized = cv2.resize(imgB, (w, h))
    imgA_blur = cv2.GaussianBlur(imgA, (3,3), 0)
    imgB_blur = cv2.GaussianBlur(imgB_resized, (3,3), 0)
    try:
        s = ssim(imgA_blur, imgB_blur)
    except Exception as e:
        return 0.0, f"SSIM calculation failed: {e}"
    feedback = "Good structural similarity." if s > 0.6 else "Low similarity. Focus on matching key strokes and proportions."
    return float(s), feedback

@app.post("/evaluate-image")
async def evaluate_image(file: UploadFile = File(...), reference: UploadFile = File(None)):
    tmp_dir = "/tmp/heritage_sage_eval"
    os.makedirs(tmp_dir, exist_ok=True)
    file_path = os.path.join(tmp_dir, f"{uuid.uuid4()}_{file.filename}")
    with open(file_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    ref_path = None
    if reference:
        ref_path = os.path.join(tmp_dir, f"ref_{uuid.uuid4()}_{reference.filename}")
        with open(ref_path, "wb") as buff:
            shutil.copyfileobj(reference.file, buff)

    if ref_path is None:
        return JSONResponse(status_code=200, content={ "score": 0.5, "feedback": "No reference provided; returned default score." })

    score, feedback = score_against_reference(file_path, ref_path)
    return JSONResponse(status_code=200, content={ "score": score, "feedback": feedback })

if __name__ == '__main__':
    uvicorn.run(app, host='0.0.0.0', port=8001)
