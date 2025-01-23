"""
  Utility functions for tag processing and text similarity.
"""
from .similarity import are_tags_close, suggest_common_tag
from .text_preprocessing import preprocess_text


def reduce_similar_tags(tags: list[str], threshold: float = 0.7) -> list[str]:
  """
    Given a list of tags, return a reduced list where similar tags are grouped 
    into one suggested common tag. The similarity threshold can be adjusted.
  """
  if not tags:
    return []

  # Process and deduplicate input tags
  processed_tags = list(set(preprocess_text(tag) for tag in tags))

  # Initialize variables to track groups of similar tags
  grouped_tags = []
  reduced_tags = []

  for tag in processed_tags:
    # Skip tags already grouped
    if any(tag in group for group in grouped_tags):
      continue

    # Find similar tags for the current tag
    similar_group = [
        t for t in processed_tags if are_tags_close(tag, t, threshold)
    ]
    if len(similar_group) > 1:
      # Suggest a common tag for similar tags
      common_tag = suggest_common_tag(similar_group)
      reduced_tags.append(common_tag)
      grouped_tags.append(similar_group)
    else:
      reduced_tags.append(tag)

  return list(set(reduced_tags))
