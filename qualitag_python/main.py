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

import os

import my_functions as mf
from flask import Flask, request, jsonify

app = Flask(__name__)


@app.route("/api/process", methods=["GET"])
def process_data():
  data = request.args.get("data")
  # Simulate processing and returning a result
  result = f"Processed data: {data.upper()}"
  return jsonify(result=result)


@app.route("/api/krippendorff", methods=["POST"])
def krippendorff_compute():
  try:
    received = request.json  # Directly access the JSON payload
    print("Data received:", received)

    alpha_value = mf.calculate_krippendorff_alpha(received)
    if alpha_value is None:
      print(
          jsonify(error=("Krippendorff's alpha could not be calculated due to "
                         "insufficient data.")), 400)

      return jsonify(
          error=("Krippendorff's alpha could not be calculated due to "
                 "insufficient data.")), 400

    print("Alpha value:", alpha_value)
    return jsonify(alpha=alpha_value)
  except KeyError as e:
    return jsonify(error=f"Key error: {str(e)}"), 400
  except TypeError as e:
    return jsonify(error=f"Type error: {str(e)}"), 400
  except ValueError as e:
    return jsonify(error=f"Value error: {str(e)}"), 400


@app.route("/api/process-tags", methods=["POST"])
def process_tags():
  try:
    received = request.json  # Directly access the JSON payload

    result = mf.reduce_similar_tags(received, threshold=0.7)
    return jsonify(result=result)
  except KeyError as e:
    return jsonify(error=f"Key error: {str(e)}"), 400
  except TypeError as e:
    return jsonify(error=f"Type error: {str(e)}"), 400
  except ValueError as e:
    return jsonify(error=f"Value error: {str(e)}"), 400


if __name__ == "__main__":
  host = os.getenv("FLASK_RUN_HOST", "localhost")
  app.run(host=host, port=5000)
