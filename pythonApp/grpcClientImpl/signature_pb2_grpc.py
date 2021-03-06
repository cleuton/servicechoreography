# Generated by the gRPC Python protocol compiler plugin. DO NOT EDIT!
import grpc

import signature_pb2 as signature__pb2


class SignVerifyStub(object):
  # missing associated documentation comment in .proto file
  pass

  def __init__(self, channel):
    """Constructor.

    Args:
      channel: A grpc.Channel.
    """
    self.VerifySignature = channel.unary_unary(
        '/signverify.SignVerify/VerifySignature',
        request_serializer=signature__pb2.SignVerifyRequest.SerializeToString,
        response_deserializer=signature__pb2.SignVerifyResponse.FromString,
        )


class SignVerifyServicer(object):
  # missing associated documentation comment in .proto file
  pass

  def VerifySignature(self, request, context):
    # missing associated documentation comment in .proto file
    pass
    context.set_code(grpc.StatusCode.UNIMPLEMENTED)
    context.set_details('Method not implemented!')
    raise NotImplementedError('Method not implemented!')


def add_SignVerifyServicer_to_server(servicer, server):
  rpc_method_handlers = {
      'VerifySignature': grpc.unary_unary_rpc_method_handler(
          servicer.VerifySignature,
          request_deserializer=signature__pb2.SignVerifyRequest.FromString,
          response_serializer=signature__pb2.SignVerifyResponse.SerializeToString,
      ),
  }
  generic_handler = grpc.method_handlers_generic_handler(
      'signverify.SignVerify', rpc_method_handlers)
  server.add_generic_rpc_handlers((generic_handler,))
