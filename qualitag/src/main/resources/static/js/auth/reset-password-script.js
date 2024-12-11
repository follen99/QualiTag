document.getElementById('resetPasswordForm').addEventListener('submit', async function (event) {
  event.preventDefault();

  // Extract token from URL query parameter
  const urlParams = new URLSearchParams(window.location.search);
  const token = urlParams.get("token");

  const formData = new FormData(event.target);
  const data = Object.fromEntries(formData.entries());

  try {
    const response = await fetch('/api/v1/auth/reset-password?token=' + encodeURIComponent(token), {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data)
    });

    const responseData = await response.json();

    if (response.ok) {
      alert(responseData.msg || 'Password updated successfully.');
      window.location.href = '/';
    } else {
      alert('Update failed: ' + (responseData.msg || 'Unknown error'));
    }
  } catch (error) {
    alert('An error occurred: ' + error.message);
  }
});