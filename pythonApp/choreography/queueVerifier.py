from flask import Flask,jsonify
from flask import request, abort
from flask import Response
import io
import os
import pika
import json

application = Flask(__name__)


#'8ed7b4235f21db78c92e69082df3874c03d4135515cb04ff1592e66d70999d56c504dd8f6dd275f870873639ea8803ddae40272465101935a19a1877c0f07715f0cb65beb839dbf33d691acc30bd3a1af6bcc42a1b86215c6cc230e7f2ff2bcff0452df651c89659a2a6f4c8364f86ab2fccac5d7ca4d15654839aa9723e9c70f15f0699037e0745947f5253545f66b7cd3b549f9e94066c319c4e5945dddf6bafebf165c984cf60c2b4fb4ae8aade21f0a88a637161c9cb6314cf4fd42ad4c4a50337b911126f188e77dc83aeaed97338a5ee53ddc0c3575041413ab11655129f15418838a2a531516276cda5df1f814f3c3ae8986c6663533a3f31aba73e19'
def verify(textFile,signature):

    connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
    channel = connection.channel()

    channel.queue_declare(queue='verifySignature', durable=True)
    with io.open(textFile, 'rt', newline='') as f:
        content = f.read()
        req={"text" : content, "hexSignature" : signature}
        jmsg=json.dumps(req)    
        channel.basic_publish(exchange='',
            routing_key='verifySignature',
            body=jmsg)


@application.route('/api/signature', methods=['POST'])
def invoke():
    print(request)
    verify(request.json['textFile'],request.json['hexSignature'])
    returnValue={"msg" : "Verification request sent"}
    status=200
    response = application.response_class(
        response=json.dumps(returnValue),
        status=status,
        mimetype='application/json'
    )    
    return response

@application.route('/api/signature', methods=['GET'])
def check():
    connection = pika.BlockingConnection()
    channel = connection.channel()
    method_frame, header_frame, body = channel.basic_get('signature_response')
    print(method_frame)
    print(body)
    if method_frame:
        jbody=json.loads(body)
        
        channel.basic_ack(method_frame.delivery_tag)
        if jbody['response']==1:
            returnValue={"verified": True,"response": True}
        else:
            returnValue={"verified": True,"response": False}
    else:
        returnValue={"verified": False,"response": False}
    status=200
    response = application.response_class(
        response=json.dumps(returnValue),
        status=status,
        mimetype='application/json'
    )    
    return response

#
# Main hook not using threads, but workers
#
portn = os.getenv('VERIFY_SERVER_PORT',5000)
if __name__ == '__main__':
    application.run(host='0.0.0.0',port=portn, threaded=False, debug=True, use_reloader=False)

