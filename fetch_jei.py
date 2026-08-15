import urllib.request
import json
url = "https://api.modrinth.com/v2/project/jei/version"
req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
with urllib.request.urlopen(req) as response:
    data = json.loads(response.read().decode())
    versions = [v['version_number'] for v in data if 'neoforge' in v['loaders'] and ('1.21.3' in v['game_versions'] or '1.21.1' in v['game_versions'])]
    print("JEI Versions:")
    for v in versions[:10]:
        print(v)
