"""
  This module provides functions to check the similarity between tags.
"""
from sentence_transformers import SentenceTransformer, util
from .text_preprocessing import preprocess_text

# Load a pre-trained model (this model balances speed and accuracy)
model = SentenceTransformer("all-MiniLM-L6-v2")


def are_tags_close(tag1: str, tag2: str, threshold: float = 0.7) -> bool:
  """
    Check if two tags are semantically close based on cosine similarity.
  """
  tag1, tag2 = preprocess_text(tag1), preprocess_text(tag2)
  embedding1 = model.encode(tag1, convert_to_tensor=True)
  embedding2 = model.encode(tag2, convert_to_tensor=True)
  similarity = util.cos_sim(embedding1, embedding2)
  return similarity.item() > threshold


def suggest_common_tag(tags: list[str]) -> str:
  """
    Suggest a common tag that could represent all input tags.
  """
  if not tags:
    return "No tags provided"
  processed_tags = [preprocess_text(tag) for tag in tags]
  embeddings = model.encode(processed_tags, convert_to_tensor=True)
  avg_embedding = sum(embeddings) / len(embeddings)

  # Finding the closest tag to the average embedding
  similarities = util.cos_sim(avg_embedding, embeddings).squeeze().tolist()
  best_index = similarities.index(max(similarities))
  return tags[best_index]  # Return the tag closest to the average embedding
