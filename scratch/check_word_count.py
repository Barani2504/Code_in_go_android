import json
import glob

for path in sorted(glob.glob('app/src/main/assets/curriculum/*.json')):
    with open(path, encoding='utf-8') as f:
        d = json.load(f)
    for card in d.get('lessonCards', []):
        wc = len(card['coreConcept'].split())
        if wc > 40:
            print(f"Chapter {d['id']} - Card '{card['title']}': {wc} words")
            print(f"Text: {card['coreConcept']}\n")
