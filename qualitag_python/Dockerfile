# Use a lightweight Python image
FROM python:3.11-slim
LABEL author="Mattia"

# Set working directory
WORKDIR /app

# Copy requirements.txt and install dependencies
COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

# Copy the application code
COPY . .

# Expose the port for the web server
EXPOSE 5000

# Define the entry point and default arguments
ENTRYPOINT ["python", "main.py"]
CMD ["--port", "5000"]
