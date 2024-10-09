
def decryption(text_encrypted_base64: str, private_key: bytes):
    # 字符串指定编码（转为bytes）
    text_encrypted_base64 = text_encrypted_base64.encode('utf-8')
    # base64解码
    text_encrypted = base64.b64decode(text_encrypted_base64)
    # 构建私钥对象
    cipher_private = PKCS1_v1_5.new(RSA.importKey(private_key))
    # 解密（bytes）
    text_decrypted = cipher_private.decrypt(text_encrypted, Random.new().read)
    # 解码为字符串
    text_decrypted = text_decrypted.decode()
    return text_decrypted

if __name__ == '__main__':
    pra = '''-----BEGIN PRIVATE KEY-----\n
          MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCwY7m/Rno2aHTI\n
          P1TdhHcd2gYcWOpKtuPhpqwN2wmuv0XTmOa+jDW4N56yRLLjGlGLM4IHO+WIgdri\n
          2pscxITP1XAgPtQihCVKg9VAejAou+dZXPR5tFnlwbD0pqw+yMKNmm/mPx9HKAra\n
          50+A0Ll6nLX0xaTtWcPO3J+Yz32jCCpCNg46hl9lPbydhAq1yu+k5GwI8maKNbHk\n
          IojjyzCMgc01mpOk9fDWyQmwHmhiLFNJ/N2BLe7BGmz/ZaWsXwqKUdpJRPYdqWyl\n
          ZtoF9o0HbVFTHmCi76xcoYmCBQ1kBY9Qg8zuOHF1DZwbkaeEF7ZyYCJ2jrUW6AXi\n
          rGHpY45jAgMBAAECggEALDZi+aHFwlt+Yi8Vq4ynetEKVRKob1/WUNpBKBvuGmIR\n
          M4+QztRAJtOyMIbtu+T96YFYp02JJCVAYzpeROCxVZLvTvXQoHI1Eq9mxYmbBqrK\n
          u2iEUvk0y1U4cKpSZE5YDbxXLgRGZ8SOq/3svKIUy7n6FcdwppmobfJ5AomHuVRI\n
          96W77z1wncrQStj+Kjh7LNXwl4VclOpXPyQ6bI8O6+G605NjhGwUuqmTE232pMOG\n
          GzYtLtwZeeXsfe99Gmh4WzOwUin4TfVgRgZcbZpEh0Etlj4ASjaRg1NL3ZhcN8HA\n
          e4or/ry3KF23rBQuyN4xrX13ywgkMaEWySn+hCAS6QKBgQDeb8Q2LgyRL4QjlBp/\n
          06xplcYZrWwwbWn0pp5W+uKbtrwWGnR/M89rnEtE6Qd3fACCAxGKEZhQh7nEht+e\n
          O5EAZa2H8pwD6FSLlurhfx2od9cVe6mPAtUgT3p7wBBZS8SIOGE13bXOi7fjnUIF\n
          wuwCyxSMANcVrWF7NudTk2C+lQKBgQDLAUOUzcJx+sN+DOYPLGILHHTmeinPyHC5\n
          vfADNSNvXy7JrXLscMhf4Jzw4XEUC6X3uiTHQZ9eVFD1dVw7XlQo+T1NdoZfkCUk\n
          M8RiEBXnC9PvBWObD+f/HEWobnfksyEXl+IkOgflzBHh60r9a2FtX8cOXycLpbj3\n
          IeKTx9/zFwKBgQCfEOoNnKyb/pDOKJEyrBp1fwelQSFHGFdW/vfoV41JsUmbvNtm\n
          Q5Sm7aPd6Y7PYTuzvaIDTEXSAN1VruWm8A3nbM7FkzcCjFg5YnkhPJfm65xB3qcI\n
          gAwNCQkcCeMnGEm46CiMLrpetAeSm9ik6hmAfd15qPgmplA5nq+aUqAyaQKBgDeY\n
          VQW/g+zrG/a9WZP2N7OLS4iDFEhPQG/d0IlQWC/8BTg+RCb0jCuVu+VMGEfBGObL\n
          F59qHVccCD/wsB2iK7I878jzGMG7HDDZ7IJenWD1IElpVGn25TY8a+dAuZzsYbew\n
          ZMI/EkKgrJUOdy8H5VqqzDznWi4aaJllskN+jZppAoGAe3ejGhWzKqQm/ep1B/l0\n
          KlRKwGBBUfm1jP8Q9t39isjw1u4+6o2gTtBMaLaFg6TvwUwQdkHNq2diFfWX1Gqo\n
          GKzrPjDQOVi0eG5G3pgVZsrkHYm9CEssQiDaRuYjyVYIrRLPNb8X7Ts2Kq7l5ZDE\n
          qBY6G9mAekT22FmMk7Y1MeA=\n
          -----END PRIVATE KEY-----'''
    text = "f7ZLGovDTI2kCX3x6QlCM2VwhoLyYl8Y/dohWTqKGJ1ivLFrU5oHLd8b/urzea9Iq4gDWMkTkUYOLaO5V1k4QoKpzI5DbnSCv7TcAGb1mlqT5WJGm4g96B5V/HeabzdQ3duh6IKzYrzgm00xqkKozRlp7MmmnLwRZJ6P1+SNmSSvOcrpRYm3QB90rLYriJRLnvE4E8jqnZfYNiFasIbJsB5Gkl2rEv7FlrjPLfTfsI6JsfMjzjdZoMkyUf9N56kkpAMobJ3L4hEksLva17c1avqyCugqkdhtMX5UMVGtRsoDR6vDObyjl0OViXZHS+ClLCFW1J/+typA+ykMEIX1rw=="
    print(decryption(text, pra.encode()))


