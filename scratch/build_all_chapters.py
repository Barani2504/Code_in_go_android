import json
import os

CURRICULUM_DIR = r"app\src\main\assets\curriculum"
os.makedirs(CURRICULUM_DIR, exist_ok=True)

# Helper function to create a standardized chapter
def create_chapter(num, title, realm_id, realm_title, unit, covers, vis_id, vis_title, prereq, goals, cards, questions, recap):
    return {
        "id": num,
        "number": num,
        "realmId": realm_id,
        "realmTitle": realm_title,
        "title": title,
        "syllabusUnit": unit,
        "covers": covers,
        "visualizerId": vis_id,
        "visualizerTitle": vis_title,
        "prerequisites": prereq,
        "estimatedMinutes": 5,
        "learningGoals": goals,
        "lessonCards": cards,
        "questions": questions,
        "ashRecap": recap
    }

chapters = []

# Chapters 1 to 5 (Realm 1) are defined in generate_curriculum.py
# Let's import or build the complete 25 chapters catalog.
