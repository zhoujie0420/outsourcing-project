from translate import Translator
from django.http import JsonResponse

# 创建Translator对象
translator = Translator(from_lang="en", to_lang="zh")


def index(request):
    if request.method == 'GET':
        # 从URL参数中获取图像文件路径
        textRes = request.GET.get('text', '')
        # 翻译识别出的文本
        translated_text = translator.translate(textRes)
        print("Original text:", textRes)
        print("Translated text:", translated_text)
        return JsonResponse({'recognized_text': textRes, 'translated_text': translated_text})
    else:
        return JsonResponse({'error': 'Unsupported request method'}, status=405)
