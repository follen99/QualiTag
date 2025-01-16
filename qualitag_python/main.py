"""
  Just a simple hello world program.
"""
import os
from flask import Flask, request, jsonify

app = Flask(__name__)


@app.route("/api/process", methods=["GET"])
def process_data():
  data = request.args.get("data")
  # Simulate processing and returning a result
  result = f"Processed data: {data.upper()}"
  return jsonify(result=result)


if __name__ == "__main__":
  host = os.getenv("FLASK_RUN_HOST", "localhost")
  app.run(host=host, port=5000)
