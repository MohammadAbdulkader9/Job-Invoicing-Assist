import json

import dummydata
from flask import Flask, jsonify, request

app = Flask(__name__)

errorResponse = {
    "ErrorInformation": {
        "Code": "123",
        "Error": "321",
        "Message": "Error",
    }
}

invoices = dummydata.invoices
customers = dummydata.customers


@app.route("/3/invoices", methods=["POST"])
def postInvoice():
    last_id = invoices[-1]["id"]
    new_invoice = json.loads(json.dumps(request.json))
    new_invoice["id"] = last_id + 1
    if new_invoice["CustomerNumber"] == "":
        return jsonify(errorResponse), 400
    invoices.append(new_invoice)
    return jsonify(new_invoice), 201


@app.route("/3/invoices/<int:invoice_id>", methods=["GET", "PUT"])
def getInvoice(invoice_id):
    request.headers.get("Content-Type")

    for invoice in invoices:
        if invoice["id"] == invoice_id:
            if request.method == "PUT":
                invoices[invoice_id] = json.loads(json.dumps(request.json))
                return jsonify(request.json)

            return invoice, 200

    return jsonify(
        {
            "ErrorInformation": {
                "Code": "123",
                "Error": "321",
                "Message": f"Invoice with ID {invoice_id} not found",
            }
        }
    ), 404
