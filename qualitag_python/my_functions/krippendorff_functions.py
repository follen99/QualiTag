"""
  This module contains functions to calculate Krippendorff's Alpha
"""
import krippendorff
import numpy as np


def calculate_krippendorff_alpha(data):
  # Extract unique tags across all raters
  unique_tags = sorted(
      {tag for item in data for rater in item for tag in rater})
  print(f"Unique tags: {unique_tags}\n")

  # Initialize binary matrix for each item
  # (each row represents a rater, each column a tag)
  binary_matrix = []
  for item in data:
    for rater_tags in item:
      row = [1 if tag in rater_tags else 0 for tag in unique_tags]
      binary_matrix.append(row)

  # Each row represents a tag. Coulumns are grouped by items
  # (es. first 4 columns are for item 1)
  binary_matrix = np.array(binary_matrix).T
  print(f"Binary representation for all items:\n{binary_matrix}\n")
  print("")

  # Compute Krippendorff"s Alpha for the entire dataset
  print("Calculating Krippendorff's Alpha...")
  alpha_calculated = krippendorff.alpha(reliability_data=binary_matrix,
                                        level_of_measurement="nominal")
  print(f"Krippendorff's alpha for nominal metric: {alpha_calculated}")

  # Result with current example: -0.12673611111111116
  return alpha_calculated
