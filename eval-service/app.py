from fastapi import FastAPI, File, UploadFile, Form
from fastapi.responses import JSONResponse
from pydantic import BaseModel
import uvicorn
import os
import uuid
import numpy as np
import cv2
from skimage.metrics import structural_similarity as ssim

app = FastAPI(title="heritage-sage-eval", version="0.3")

TMP_DIR = "/tmp/heritage_sage_eval"


class EvalResponse(BaseModel):
    score: float
    feedback: str


def _save_upload(upload: UploadFile, prefix: str = "") -> str:
    os.makedirs(TMP_DIR, exist_ok=True)
    path = os.path.join(TMP_DIR, f"{prefix}{uuid.uuid4()}_{upload.filename or 'img'}")
    with open(path, "wb") as f:
        f.write(upload.file.read())
    return path


def _cleanup(*paths: str) -> None:
    for p in paths:
        try:
            if p and os.path.exists(p):
                os.remove(p)
        except OSError:
            pass


def _tiered_feedback(score: float, skill_name: str) -> str:
    skill = skill_name.strip().lower() if skill_name else "skill"
    if score >= 0.80:
        return (
            f"Excellent work on {skill}! Your submission closely matches the reference. "
            "Try a more advanced variation next."
        )
    elif score >= 0.65:
        return (
            f"Good effort on {skill}. The overall structure is solid. "
            "Pay attention to finer details and proportions."
        )
    elif score >= 0.50:
        return (
            f"Decent start with {skill}. Key shapes are recognisable but consistency needs work. "
            "Slow down and focus on one element at a time."
        )
    elif score >= 0.35:
        return (
            f"Keep practising {skill}. The main forms are off — go back to basics and compare "
            "your work against the reference stroke by stroke."
        )
    else:
        return (
            f"The submission diverges significantly from the {skill} reference. "
            "Study the reference carefully and restart with simpler components."
        )


def _compute_score(file_path: str, ref_path: str) -> tuple[float, str]:
    imgA = cv2.imread(ref_path, cv2.IMREAD_GRAYSCALE)
    imgB = cv2.imread(file_path, cv2.IMREAD_GRAYSCALE)
    if imgA is None or imgB is None:
        return 0.0, "Could not decode one or both images."

    h, w = imgA.shape
    imgB = cv2.resize(imgB, (w, h))

    imgA = cv2.GaussianBlur(imgA, (3, 3), 0)
    imgB = cv2.GaussianBlur(imgB, (3, 3), 0)

    try:
        raw = ssim(imgA, imgB, data_range=255)
    except Exception as e:
        return 0.0, f"SSIM calculation failed: {e}"

    score = float(np.clip(raw, 0.0, 1.0))
    return score, ""


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/evaluate-image", response_model=EvalResponse)
async def evaluate_image(
    file: UploadFile = File(...),
    reference: UploadFile = File(None),
    skill_name: str = Form(default=""),
):
    if reference is None:
        return EvalResponse(score=0.5, feedback="No reference image provided; returned default score.")

    file_path = ref_path = None
    try:
        file_path = _save_upload(file)
        ref_path = _save_upload(reference, prefix="ref_")
        score, err = _compute_score(file_path, ref_path)
        if err:
            return EvalResponse(score=score, feedback=err)
        return EvalResponse(score=score, feedback=_tiered_feedback(score, skill_name))
    finally:
        _cleanup(file_path, ref_path)


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8001)
