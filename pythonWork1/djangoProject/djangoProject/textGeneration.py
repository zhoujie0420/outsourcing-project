import jieba
from django.http import JsonResponse
from snownlp import SnowNLP
import json
import re


# 假设这是我们预先训练好的文本分类模型的接口
def classify_text(text):
    # 基于关键词和短语的规则来提取信息
    info = {
        '性别': '未知',
        '年龄': '未知',
        '职业': '未知',
        '家庭背景': '未知',
        '性格特点': {
            '勇敢': False,
            '聪明': False,
            '善良': False,
            '孤独': False
        }
    }

    # 提取性别
    if '他' in text or '男' in text:
        info['性别'] = '男'
    elif '她' in text or '女' in text:
        info['性别'] = '女'

    # 提取年龄（假设描述中有类似于"30岁"这样的年龄信息）
    age_match = re.search(r'(\d{1,2})岁', text)
    if age_match:
        info['年龄'] = age_match.group(1)

    # 提取职业
    professions = ['警察', '医生', '教师', '律师', '学生']
    for profession in professions:
        if profession in text:
            info['职业'] = profession
            break

    # 提取家庭背景
    backgrounds = ['富裕家庭', '普通家庭', '贫困家庭']
    for background in backgrounds:
        if background in text:
            info['家庭背景'] = background
            break

    # 提取性格特点
    traits = {
        '勇敢': ['勇敢', '无畏', '勇气'],
        '聪明': ['聪明', '智慧', '聪慧'],
        '善良': ['善良', '仁慈', '友好'],
        '孤独': ['孤独', '寂寞', '孤单']
    }
    for trait, keywords in traits.items():
        for keyword in keywords:
            if keyword in text:
                info['性格特点'][trait] = True
                break

    return info


def extract_character_description(text, character_name):
    # 简单提取包含角色名字的句子作为描述
    sentences = text.split('。')
    description = "。".join([sent for sent in sentences if character_name in sent])
    return description


def analyze_characters(text_file_path, custom_dict_path):
    # Load custom dictionary
    jieba.load_userdict(custom_dict_path)

    # Read character names list
    with open(custom_dict_path, 'r', encoding='utf-8') as file:
        character_names = set(line.strip() for line in file)

    # Read text data
    try:
        with open(text_file_path, 'r', encoding='utf-8') as file:
            text = file.read()
    except UnicodeDecodeError:
        try:
            with open(text_file_path, 'r', encoding='gbk') as file:
                text = file.read()
        except UnicodeDecodeError as e:
            print(f"Failed to read the file with utf-8 and gbk encodings: {e}")
            raise

    # Split text into sentences by period
    sentences = text.split('。')

    # Sentiment analysis function
    def analyze_sentiment(sentence):
        s = SnowNLP(sentence)
        return s.sentiments  # Return sentiment score, range from 0 to 1, closer to 1 is more positive

    # Analyze sentiment for each sentence
    sentiments = [analyze_sentiment(sent) for sent in sentences]

    # Count sentiment for each character in each sentence
    character_sentiments = {}

    for sent, sentiment in zip(sentences, sentiments):
        words = list(jieba.cut(sent))
        characters_in_sentence = character_names.intersection(words)
        for character_name in characters_in_sentence:
            if character_name not in character_sentiments:
                character_sentiments[character_name] = []
            character_sentiments[character_name].append(sentiment)

    # Calculate average sentiment score for each character
    character_avg_sentiments = {}
    for character, sentiment_list in character_sentiments.items():
        avg_sentiment = sum(sentiment_list) / len(sentiment_list)
        character_avg_sentiments[character] = avg_sentiment

    # Classify characters
    positive_threshold = 0.6
    negative_threshold = 0.4
    character_classification = {}
    for character, avg_sentiment in character_avg_sentiments.items():
        if avg_sentiment > positive_threshold:
            character_classification[character] = '正派'
        elif avg_sentiment < negative_threshold:
            character_classification[character] = '反派'
        else:
            character_classification[character] = '中立'

    # Generate character basic information and personality traits using text classification
    character_info = {}
    for character in character_classification.keys():
        # Extract character description from text
        character_description = extract_character_description(text, character)

        # 使用文本分类模型获取角色信息
        classified_info = classify_text(character_description)

        # 更新角色信息
        character_info[character] = {
            '基本信息': {
                '姓名': character,
                '性别': classified_info['性别'],
                '年龄': classified_info['年龄'],
                '职业': classified_info['职业'],
                '家庭背景': classified_info['家庭背景']
            },
            '性格特点': classified_info['性格特点']
        }

    return character_classification, character_info


def index(request):
    if request.method == 'GET':
        # Example usage
        text_file_path = '/Users/jiezhou/Downloads/' + request.GET.get('url', '')
        custom_dict_path = '/Users/jiezhou/Downloads/' + 'extra' + request.GET.get('url', '')
        # text_file_path = '/Users/jiezhou/Downloads/test2.txt'
        # custom_dict_path = '/Users/jiezhou/Downloads/test2pro.txt'
        character_classification, character_info = analyze_characters(text_file_path, custom_dict_path)
        print(json.dumps(character_classification, ensure_ascii=False, indent=4))
        print(json.dumps(character_info, ensure_ascii=False, indent=4))
        return JsonResponse({'character_classification': json.dumps(character_classification, ensure_ascii=False, indent=4),
                             'character_info': json.dumps(character_info, ensure_ascii=False, indent=4)})
    else:
        return JsonResponse({'error': 'Unsupported request method'}, status=405)
