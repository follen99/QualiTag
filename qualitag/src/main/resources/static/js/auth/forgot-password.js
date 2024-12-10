document.getElementById("forgotPasswordForm").addEventListener("submit", function (event) {
  event.preventDefault(); // Prevent the default form submission

  const email = document.getElementById("email").value;

  // Validate email
  if (!email) {
    alert("Please enter a valid email address.");
    return;
  }

  // Send a POST request to the server
  fetch("/api/v1/auth/forgot-password", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ email: email }),
  })
  .then((response) => {
    if (response.ok) {
      alert("Password reset email sent successfully.");
    } else {
      alert("Error sending email. Please try again.");
    }
  })
  .catch((error) => {
    console.error("Error:", error);
    alert("An error occurred. Please try again.");
  });
});
