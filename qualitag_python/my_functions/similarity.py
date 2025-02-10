"""
  This module provides functions to check the similarity between tags.
"""
from sentence_transformers import SentenceTransformer, util
from .text_preprocessing import preprocess_text

# Load a pre-trained model (this model balances speed and accuracy)
model = SentenceTransformer("all-MiniLM-L6-v2")


# TODO: Modify to get list of tags as tag 1 and compute with average embedding
def are_tags_close(tag1: str, tag2: str, threshold: float = 0.7) -> bool:
  """
    Check if two tags are semantically close based on cosine similarity.
  """
  tag1, tag2 = preprocess_text(tag1), preprocess_text(tag2)
  embedding1 = model.encode(tag1, convert_to_tensor=True)
  embedding2 = model.encode(tag2, convert_to_tensor=True)
  similarity = util.cos_sim(embedding1, embedding2)

  if similarity.item() > threshold:
    print(f"'{tag1}' and '{tag2}' are similar: {similarity.item()}")
  else:
    print(f"'{tag1}' and '{tag2}' are not similar: {similarity.item()}")

  return similarity.item() > threshold


# ...existing code...


def group_tags_by_average_similarity(tags: list[str],
                                     threshold: float = 0.7) -> list[list[str]]:
  """
    Groups tags by checking the average similarity of a new tag
    against all tags in the group.
    """
  grouped_tags = []
  embeddings = [(tag, model.encode(preprocess_text(tag),
                                   convert_to_tensor=True)) for tag in tags]

  def average_similarity(new_emb, group_embs) -> float:
    sims = [util.cos_sim(new_emb, emb).item() for emb in group_embs]
    return sum(sims) / len(sims) if sims else 0.0

  for tag, emb in embeddings:
    added = False
    for group in grouped_tags:
      group_embs = [
          model.encode(preprocess_text(t), convert_to_tensor=True)
          for t in group
      ]
      if average_similarity(emb, group_embs) >= threshold:
        group.append(tag)
        added = True
        break
    if not added:
      grouped_tags.append([tag])

  return grouped_tags


def suggest_common_tag(tags: list[str]) -> str:
  """
    Suggest a common tag that could represent all input tags, 
    preferring shorter tags.
  """
  if not tags:
    return "No tags provided"

  print(f"\nReceived tags: {tags}")

  processed_tags = [preprocess_text(tag) for tag in tags]
  embeddings = model.encode(processed_tags, convert_to_tensor=True)
  avg_embedding = sum(embeddings) / len(embeddings)

  # Finding the closest tag to the average embedding
  similarities = util.cos_sim(avg_embedding, embeddings).squeeze().tolist()

  # Adjust similarities based on tag length
  # Shorter tags are preferred by reducing their similarity score less
  length_penalties = [len(tag) for tag in tags]
  max_length = max(length_penalties)
  adjusted_similarities = [
      similarity * (1 - (len(tag) / (2 * max_length)))
      for similarity, tag in zip(similarities, tags)
  ]

  # Print each tag with its original and adjusted similarity
  print("Tag Analysis:")
  for tag, similarity, adjusted_similarity in zip(tags, similarities,
                                                  adjusted_similarities):
    print(f"Tag: {tag} \t| Original Similarity: {similarity:.4f} \t| \
          Adjusted Similarity: {adjusted_similarity:.4f}")

  best_index = adjusted_similarities.index(max(adjusted_similarities))

  print(f"Selected tag: {tags[best_index]}")

  # Return the tag closest to the average embedding with preference
  # for shorter tags
  return tags[best_index]
