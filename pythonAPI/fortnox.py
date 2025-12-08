import json

from flask import Flask, jsonify, request

app = Flask(__name__)

errorResponse = {
    "ErrorInformation": {
        "Code": "123",
        "Error": "321",
        "Message": "Error",
    }
}

invoices = [
    {
        "id": 1,
        "Address1": "123 Main St",
        "Address2": "Suite 100",
        "City": "Anytown",
        "Country": "Sweden",
        "CustomerName": "John Doe",
        "CustomerNumber": "12345",
        "Currency": "SEK",
    },
    {
        "id": 2,
        "Address1": "456 Oak Ave",
        "Address2": "Apartment 20B",
        "City": "Gothenburg",
        "Country": "Sweden",
        "CustomerName": "Jane Smith",
        "CustomerNumber": "67890",
        "Currency": "SEK",
    },
    {
        "id": 3,
        "Address1": "789 Pine Ln",
        "Address2": "Unit 5",
        "City": "Malmo",
        "Country": "Sweden",
        "CustomerName": "Peter Jones",
        "CustomerNumber": "11223",
        "Currency": "EUR",
    },
    {
        "id": 4,
        "Address1": "101 Elm Rd",
        "Address2": "Building C",
        "City": "Stockholm",
        "Country": "Sweden",
        "CustomerName": "Anna Karlsson",
        "CustomerNumber": "44556",
        "Currency": "SEK",
    },
    {
        "id": 5,
        "Address1": "222 Birch St",
        "Address2": "Floor 3",
        "City": "Uppsala",
        "Country": "Sweden",
        "CustomerName": "Erik Svensson",
        "CustomerNumber": "77889",
        "Currency": "SEK",
    },
]


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
