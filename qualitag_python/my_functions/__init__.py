"""
  Package to store all the functions that are used in the project.
"""

__author__ = "Mattia Marino"
__version__ = "0.1.0"
__license__ = "GPL-3.0"

from .krippendorff_functions import calculate_krippendorff_alpha

from .similarity import are_tags_close, suggest_common_tag

from .text_preprocessing import preprocess_text

from .utils import reduce_similar_tags
