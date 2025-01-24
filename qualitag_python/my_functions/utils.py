"""
  Utility functions for tag processing and text similarity.
"""
from .similarity import group_tags_by_average_similarity, suggest_common_tag
from .text_preprocessing import preprocess_text


def reduce_similar_tags(tags: list[str], threshold: float = 0.7) -> list[str]:
  """
    Given a list of tags, return a reduced list where similar tags are grouped 
    into one suggested common tag. The similarity threshold can be adjusted.
  """
  if not tags:
    return []

  # Process and deduplicate input tags
  processed_tags = sorted(list(set(preprocess_text(tag) for tag in tags)))
  print(f"Processed tags: {processed_tags}")

  grouped_tags = group_tags_by_average_similarity(processed_tags, threshold)
  print(f"\n\nGrouped tags new method: {grouped_tags}\n\n")

  reduced_tags = []
  for group in grouped_tags:
    if len(group) == 1:
      reduced_tags.append(group[0])
    else:
      common_tag = suggest_common_tag(group)
      reduced_tags.append(common_tag)

  return sorted(list(set(reduced_tags)))
