{

  <@lib.endpointInfo
      id = "addAttachment"
      tag = "Attachment"
      desc = "Creates an attachment for a task." />

  "parameters" : [

    <@lib.parameter
        name = "id"
        location = "path"
        type = "string"
        required = true
        last = true
        desc = "The id of the task to add the attachment to."/>

  ],

  <@lib.requestBody
      mediaType = "multipart/form-data"
      dto = "MultiFormAttachmentDto"
      examples = ['"example-1": {
                     "summary": "Status 200 Response",
                     "value": {
                       "links": [
                         {
                           "method": "GET",
                           "href": "http://localhost:38080/rest-test/task/aTaskId/attachment/aTaskAttachmentId",
                           "rel": "self"
                         }
                       ],
                         "id": "attachmentId",
                         "name": "attachmentName",
                         "taskId": "aTaskId",
                         "description": "attachmentDescription",
                         "type": "attachmentType",
                         "url": "http://my-attachment-content-url.de",
                         "createTime":"2017-02-10T14:33:19.000+0200",
                         "removalTime":"2018-02-10T14:33:19.000+0200",
                         "rootProcessInstanceId": "aRootProcessInstanceId"
                     }
                   }'] />

  "responses" : {

    <@lib.response
        code = "200"
        dto = "AttachmentDto"
        desc = "Request successful." />

    <@lib.response
        code = "400"
        dto = "ExceptionDto"
        desc = "The task does not exists or task id is null. No content or url to remote content exists. See the
                [Introduction](/reference/rest/overview/#error-handling) for the error response format." />

    <@lib.response
        code = "403"
        dto = "ExceptionDto"
        last = true
        desc = "The history of the engine is disabled. See the [Introduction](/reference/rest/overview/#error-handling)
                for the error response format." />

  }
}
