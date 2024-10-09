import tensorflow as tf
from django.http import JsonResponse
from tensorflow.keras.layers import Embedding, LSTM, Dense
from tensorflow.keras.models import Model
from tensorflow.keras.preprocessing.text import Tokenizer
from tensorflow.keras.preprocessing.sequence import pad_sequences
import numpy as np

# 简单的数据集
data = [
    ("Hello", "你好"),
    ("Thank you", "谢谢"),
    ("Yes", "是"),
    ("No", "不是"),
]

# 添加开始和结束标记
data = [("<start> " + inp + " <end>", "<start> " + targ + " <end>") for inp, targ in data]

# 分割数据为英语和中文句子
input_texts, target_texts = zip(*data)

# 对输入和目标文本进行tokenization
tokenizer_input = Tokenizer(filters='')
tokenizer_input.fit_on_texts(input_texts)
input_sequences = tokenizer_input.texts_to_sequences(input_texts)
input_sequences = pad_sequences(input_sequences, padding='post')

tokenizer_output = Tokenizer(filters='')
tokenizer_output.fit_on_texts(target_texts)
target_sequences = tokenizer_output.texts_to_sequences(target_texts)
target_sequences = pad_sequences(target_sequences, padding='post')

# 获取词汇表大小
input_vocab_size = len(tokenizer_input.word_index) + 1
output_vocab_size = len(tokenizer_output.word_index) + 1

# 超参数
embedding_dim = 256
units = 512
batch_size = 2
epochs = 500

# 构建编码器
encoder_inputs = tf.keras.Input(shape=(None,))
encoder_embedding = Embedding(input_vocab_size, embedding_dim)(encoder_inputs)
encoder_outputs, state_h, state_c = LSTM(units, return_state=True)(encoder_embedding)
encoder_states = [state_h, state_c]

# 构建解码器
decoder_inputs = tf.keras.Input(shape=(None,))
decoder_embedding = Embedding(output_vocab_size, embedding_dim)(decoder_inputs)
decoder_lstm = LSTM(units, return_sequences=True, return_state=True)
decoder_outputs, _, _ = decoder_lstm(decoder_embedding, initial_state=encoder_states)
decoder_dense = Dense(output_vocab_size, activation='softmax')
decoder_outputs = decoder_dense(decoder_outputs)

# 构建模型
model = Model([encoder_inputs, decoder_inputs], decoder_outputs)

# 编译模型
model.compile(optimizer='rmsprop', loss='sparse_categorical_crossentropy')

# 准备目标数据，需要在目标序列上进行右移
target_sequences_shifted = np.zeros_like(target_sequences)
target_sequences_shifted[:, :-1] = target_sequences[:, 1:]

# 训练模型
model.fit([input_sequences, target_sequences], target_sequences_shifted,
          batch_size=batch_size, epochs=epochs, validation_split=0.2)

# 构建推理模型
encoder_model = Model(encoder_inputs, encoder_states)

decoder_state_input_h = tf.keras.Input(shape=(units,))
decoder_state_input_c = tf.keras.Input(shape=(units,))
decoder_states_inputs = [decoder_state_input_h, decoder_state_input_c]

decoder_outputs, state_h, state_c = decoder_lstm(
    decoder_embedding, initial_state=decoder_states_inputs)
decoder_states = [state_h, state_c]
decoder_outputs = decoder_dense(decoder_outputs)
decoder_model = Model(
    [decoder_inputs] + decoder_states_inputs,
    [decoder_outputs] + decoder_states)

# 翻译函数
def decode_sequence(input_seq):
    states_value = encoder_model.predict(input_seq)

    target_seq = np.zeros((1, 1))
    target_seq[0, 0] = tokenizer_output.word_index['<start>']

    stop_condition = False
    decoded_sentence = ''
    while not stop_condition:
        output_tokens, h, c = decoder_model.predict(
            [target_seq] + states_value)

        sampled_token_index = np.argmax(output_tokens[0, -1, :])
        sampled_char = tokenizer_output.index_word.get(sampled_token_index, '')

        if sampled_char == '<end>' or len(decoded_sentence) > 50:
            stop_condition = True
        else:
            decoded_sentence += ' ' + sampled_char

        target_seq = np.zeros((1, 1))
        target_seq[0, 0] = sampled_token_index

        states_value = [h, c]

    return decoded_sentence.strip()

# 测试翻译
for seq_index in range(len(input_sequences)):
    input_seq = input_sequences[seq_index: seq_index + 1]
    decoded_sentence = decode_sequence(input_seq)
    print('-')
    print('Input sentence:', input_texts[seq_index])
    print('Decoded sentence:', decoded_sentence)


# 定义一个预处理输入文本的函数
def preprocess_input_text(input_text):
    input_text = "<start> " + input_text + " <end>"
    input_seq = tokenizer_input.texts_to_sequences([input_text])
    input_seq = pad_sequences(input_seq, maxlen=input_sequences.shape[1], padding='post')
    return input_seq


# 修改的index函数
def index(request):
    if request.method == 'GET':
        text_res = request.GET.get('text', '')
        if not text_res:
            return JsonResponse({'error': 'No text provided'}, status=400)
        input_seq = preprocess_input_text(text_res)
        decoded_sentence = decode_sequence(input_seq)
        return JsonResponse({'input_text': text_res, 'decoded_sentence': decoded_sentence})
    else:
        return JsonResponse({'error': 'Unsupported request method'}, status=405)
