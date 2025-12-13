import json

# import dummydata
import dummydata.customers as dc
import dummydata.invoices as di
from flask import Flask, jsonify, request
from flask.helpers import send_file

app = Flask(__name__)

errorResponse = {
    "ErrorInformation": {
        "Code": "123",
        "Error": "321",
        "Message": "Error",
    }
}

invoices = di.invoices
customers = dc.customers


@app.route("/3/invoices", methods=["POST", "GET"])
def postInvoice():
    if request.method == "POST":
        last_id = invoices["Invoices"][-1]["id"]
        new_invoice = json.loads(json.dumps(request.json))
        new_invoice["id"] = last_id + 1
        if new_invoice["CustomerNumber"] == "":
            return jsonify(errorResponse), 400
        invoices["Invoices"].append(new_invoice)
        return jsonify(new_invoice), 201

    return jsonify(invoices["Invoices"])


@app.route("/3/invoices/<int:invoice_id>", methods=["GET", "PUT", "DELETE"])
def getInvoice(invoice_id):
    request.headers.get("Content-Type")
    if request.method == "DELETE":
        original_length = len(invoices["Invoice"])
        invoices["Invoices"] = [
            inv for inv in invoices["Invoices"] if inv["id"] != invoice_id
        ]
        if len(invoices["Invoice"]) < original_length:
            return "", 204

        return "", 404

    for invoice in invoices["Invoices"]:
        if invoice["id"] == invoice_id:
            if request.method == "PUT":
                invoice.update(request.json)
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


@app.route("/3/invoices/<int:invoice_id>/preview", methods=["GET"])
def getInvoicePreview(invoice_id):
    for invoice in invoices["Invoices"]:
        if invoice["id"] == invoice_id:
            return send_file("./assets/sample.pdf")

    return jsonify(errorResponse), 400
