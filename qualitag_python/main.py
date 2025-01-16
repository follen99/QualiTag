"""
This module provides a Flask web application with an API endpoint 
to compute Krippendorff's Alpha.

Functions:
  krippendorff_compute(): Handles POST requests to the /api/krippendorff 
    endpoint, computes Krippendorff's Alpha, and returns the result as JSON.
  calculate_krippendorff_alpha(data): Computes Krippendorff's Alpha for 
    the given data.

Routes:
  /api/krippendorff (POST): Accepts JSON payload, computes 
    Krippendorff's Alpha, and returns the result.

Usage:
  Run this module to start the Flask web application. 
  The default host is 'localhost' and the default port is 5000.
"""
import krippendorff
import numpy as np
import os
from flask import Flask, request, jsonify

app = Flask(__name__)


@app.route("/api/krippendorff", methods=["POST"])
def krippendorff_compute():
  try:
    received = request.json  # Directly access the JSON payload
    print(f"Data (Python): {received}")

    alpha_value = calculate_krippendorff_alpha(received)
    return jsonify(alpha=alpha_value)
  except KeyError as e:
    return jsonify(error=f"Key error: {str(e)}"), 400
  except TypeError as e:
    return jsonify(error=f"Type error: {str(e)}"), 400
  except ValueError as e:
    return jsonify(error=f"Value error: {str(e)}"), 400
  except krippendorff.KrippendorffError as e:
    return jsonify(error=f"Krippendorff error: {str(e)}"), 400


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

  # Compute Krippendorff's Alpha for the entire dataset
  print("Calculating Krippendorff's Alpha...")
  alpha_calculated = krippendorff.alpha(reliability_data=binary_matrix,
                                        level_of_measurement="nominal")
  print(f"Krippendorff's alpha for nominal metric: {alpha_calculated}")

  return alpha_calculated


if __name__ == "__main__":
  host = os.getenv("FLASK_RUN_HOST", "localhost")
  app.run(host=host, port=5000)
