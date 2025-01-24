"""
This module contains tests for the main application using pytest.

Fixtures:
    test_client: A test client for the Flask application.

Test Functions:
    test_process_data(test_client): Tests the /api/process endpoint 
        to ensure it processes data correctly.
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
  assert data["alpha"] == -0.12673611111111116


def test_process_tags(test_client):  # pylint: disable=redefined-outer-name
  test_payload = [
      "Artificial Intelligence", "AI", "Data Science", "Machine Learning",
      "Bug", "Bugged", "Buggy"
  ]
  resp = test_client.post("/api/process-tags",
                          data=json.dumps(test_payload),
                          content_type="application/json")
  assert resp.status_code == 200
  data = resp.get_json()
  assert "result" in data
  print(data["result"])
  assert data["result"] == ["ai", "buggy", "data science", "machine learning"]
