"""
	This module contains unit tests for the Krippendorff's alpha 
	computation API endpoint.

	The tests are designed to verify the correctness and robustness 
	of the Krippendorff's alpha calculation under various scenarios, 
	including normal cases, edge cases, and error cases.

	Tests included:
	- `test_krippendorff_compute`: Tests the computation with a 
			typical payload.
	- `test_krippendorff_compute_empty`: Tests the computation with an 
			empty payload.
	- `test_krippendorff_compute_insufficient`: Tests the computation 
			with insufficient data.
	- `test_krippendorff_compute_all_equal`: Tests the computation when 
			all annotations are the same.
	- `test_krippendorff_compute_disagreement`: Tests the computation 
			with complete disagreement.

	Each test sends a POST request to the `/api/krippendorff` endpoint 
	with a specific payload and asserts the correctness of the response 
	status code and the presence and value of the `alpha` and `error` 
	fields in the response JSON.
"""

import json
import pytest
from main import app


@pytest.fixture
def test_client():
  with app.test_client() as client:
    yield client


def test_krippendorff_compute(test_client):  # pylint: disable=redefined-outer-name
  test_payload = [
      [["A", "B"], ["A", "C"], ["A"], ["B", "D"]],  # Item 1
      [["B", "C", "E"], ["B", "E"], ["C"], ["B", "C", "D"]],  # Item 2
      [["A", "D"], ["D"], ["A", "B"], ["A", "C", "D"]],  # Item 3
  ]
  resp = test_client.post("/api/krippendorff",
                          data=json.dumps(test_payload),
                          content_type="application/json")
  assert resp.status_code == 200
  data = resp.get_json()
  assert "alpha" in data
  assert data["alpha"] == 0.08894939868391216


def test_krippendorff_compute_empty(test_client):  # pylint: disable=redefined-outer-name
  test_payload = [[[], [], [], []]]
  resp = test_client.post("/api/krippendorff",
                          data=json.dumps(test_payload),
                          content_type="application/json")
  assert resp.status_code == 400
  data = resp.get_json()
  assert "alpha" not in data
  assert "error" in data
  assert data["error"] == ("Krippendorff's alpha could not be "
                           "calculated due to insufficient data.")


def test_krippendorff_compute_insufficient(test_client):  # pylint: disable=redefined-outer-name
  test_payload = [[["A"], [], []], [[], ["B"], []], [[], [], ["C"]]]
  resp = test_client.post("/api/krippendorff",
                          data=json.dumps(test_payload),
                          content_type="application/json")
  assert resp.status_code == 400
  data = resp.get_json()
  assert "alpha" not in data
  assert "error" in data
  assert data["error"] == ("Krippendorff's alpha could not be "
                           "calculated due to insufficient data.")


def test_krippendorff_compute_all_equal(test_client):  # pylint: disable=redefined-outer-name
  test_payload = [[["A"], ["A"], ["A"]], [["B"], ["B"], ["B"]]]
  resp = test_client.post("/api/krippendorff",
                          data=json.dumps(test_payload),
                          content_type="application/json")
  assert resp.status_code == 200
  data = resp.get_json()
  assert "alpha" in data
  assert "error" not in data
  assert data["alpha"] == 1.0


def test_krippendorff_compute_disagreement(test_client):  # pylint: disable=redefined-outer-name
  test_payload = [[["A"], ["B"], [], []]]
  resp = test_client.post("/api/krippendorff",
                          data=json.dumps(test_payload),
                          content_type="application/json")
  assert resp.status_code == 200
  data = resp.get_json()
  assert "alpha" in data
  assert "error" not in data
  assert data["alpha"] == 0.0
