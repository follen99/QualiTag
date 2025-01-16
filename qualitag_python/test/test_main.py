"""
This module contains tests for the main application using pytest.

Fixtures:
    test_client: A test client for the Flask application.

Test Functions:
    test_process_data(test_client): Tests the /api/process endpoint 
        to ensure it processes data correctly.
"""
import pytest
from main import app


@pytest.fixture
def test_client():
  with app.test_client() as client:
    yield client


def test_process_data(test_client):  # pylint: disable=redefined-outer-name
  resp = test_client.get("/api/process?data=hello")
  assert resp.status_code == 200
  data = resp.get_json()
  assert "Processed data: HELLO" in data["result"]
