document.addEventListener('DOMContentLoaded', function () {
  const authToken = localStorage.getItem('authToken');
  if (!authToken) {
    alert('You must be logged in to access this page.');
    window.location.href = '/signin'; // Redirect to login page
  }
});