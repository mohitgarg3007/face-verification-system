from fastapi import FastAPI, UploadFile, File
import cv2
import numpy as np
import insightface
from numpy.linalg import norm
import shutil

app = FastAPI()

model = insightface.app.FaceAnalysis()
model.prepare(ctx_id=0)

def cosine_similarity(a, b):
    return np.dot(a, b) / (norm(a) * norm(b))

@app.post("/compare")
async def compare(
        image1: UploadFile = File(...),
        image2: UploadFile = File(...)):

    path1 = "img1.jpg"
    path2 = "img2.jpg"

    with open(path1, "wb") as f:
        shutil.copyfileobj(image1.file, f)

    with open(path2, "wb") as f:
        shutil.copyfileobj(image2.file, f)

    img1 = cv2.imread(path1)
    img2 = cv2.imread(path2)

    faces1 = model.get(img1)
    faces2 = model.get(img2)

    if len(faces1) == 0 or len(faces2) == 0:
        return {
            "verified": False,
            "score": 0.0,
            "message": "Face not detected"
        }

    emb1 = faces1[0].embedding
    emb2 = faces2[0].embedding

    score = float(cosine_similarity(emb1, emb2))

    return {
        "verified": score > 0.6,
        "score": round(score, 4)
    }
