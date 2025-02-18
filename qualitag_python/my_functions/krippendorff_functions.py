"""
  This module contains functions to calculate Krippendorff's Alpha
"""
import nltk
from nltk.metrics.distance import jaccard_distance


def transform_data(data):
  """
    Transform the input data into the format required by nltk's AnnotationTask
  """
  transformed_data = []
  for i, inner in enumerate(data):
    for j, tags in enumerate(inner):
      if not tags:
        continue
      else:
        transformed_data.append((f"user_{j}", f"item_{i}", frozenset(tags)))

  return transformed_data


def safe_jaccard_distance(set1, set2):
  """
    Calculate the Jaccard distance between two sets, handling empty sets
  """
  if not set1 and not set2:
    return 0.0
  return jaccard_distance(set1, set2)


def check_data(data):
  """
    Check if the input data is valid
  """
  # Iterate the List of Lists
  for inner in data:
    # Count the number of non-empty tag lists
    count = 0
    for tags in inner:
      if tags:
        count += 1
    if count > 1:
      return True

  return False


def calculate_krippendorff_alpha(data):
  """
    Calculate Krippendorff's Alpha for the given data
  """
  if not check_data(data):
    print("Invalid data")
    return None

  task = nltk.AnnotationTask(distance=safe_jaccard_distance)
  task.load_array(transform_data(data))

  return task.alpha()
