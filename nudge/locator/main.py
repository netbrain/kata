import sys
import re
import requests
import spacy


def main(args):
    if len(args) == 0:
        print("usage: \n" + sys.argv[0] + " <url> [url...]")
        exit(0)

    nlp = spacy.load('en')

    for url in args:
        text = extract_text(url)
        doc = nlp(text)
        for ent in doc.ents:
            if ent.label_ == "GPE":
                print(ent.text)


def extract_text(url):
    print(url)
    response = requests.get(url)
    if response.status_code >= 300:
        print("request returned a faulty status code: " + response.status_code)
        return

    text = response.text

    text = re.sub(r'<(script).*?</\1>(?s)', ' ', text)
    text = re.sub(r'<!(--).*?\1>(?s)', ' ', text)
    text = re.sub('<.*?>', ' ', text)
    text = re.sub('&#\d+;', ' ', text)
    text = re.sub('[\s]+', ' ', text)
    return text


main(sys.argv[1:])
