"""
  Text preprocessing functions.
"""
import gensim
import nltk
from nltk.corpus import stopwords
from nltk.stem import WordNetLemmatizer

# Ensure necessary NLTK data is downloaded
nltk.download("stopwords")
nltk.download("wordnet")

# Initialize stopwords and lemmatizer
sw = set(stopwords.words("english"))
lemmatizer = WordNetLemmatizer()


def preprocess_text(text: str) -> str:
  """
    Preprocess text data by removing stopwords, punctuation,
    numeric characters, and multiple whitespaces.

    The following steps are performed:
    - Normalization
    - Remove stopwords
    - Lemmatization

    Args:
      text (str): Input text.

    Returns:
      str: Transformed text.
  """

  # Get English stopwords

  # Convert text to lowercase
  text = text.lower()

  # Remove multiple whitespaces
  text = gensim.corpora.textcorpus.strip_multiple_whitespaces(text)

  # Filter out stopwords and lemmatize words
  filtered_words = [
      lemmatizer.lemmatize(word) for word in text.split() if word not in sw
  ]

  # Join filtered words back into a single string
  text = " ".join(filtered_words)

  # Remove punctuation
  text = gensim.parsing.preprocessing.strip_punctuation(text)

  # Remove numeric characters
  text = gensim.parsing.preprocessing.strip_numeric(text)

  # Remove multiple whitespaces again
  text = gensim.corpora.textcorpus.strip_multiple_whitespaces(text)

  return text
