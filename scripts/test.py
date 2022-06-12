import pprint

import requests
import random
import json

headers = {"Content-type": "application/json"}

if __name__ == '__main__':
    # get products
    print("Testcase 1: get products")

    response = requests.get("http://localhost:8080/products/api/products")
    products = response.json()

    assert response.status_code == 200, f"{response.content}"

    assert len(products) > 0

    print(f"get {len(products)} products")

    product1 = random.choice(products)
    product2 = random.choice(products)

    pprint.pprint(product1)

    pprint.pprint(product2)

    input("Press any key to continue...")

    print("Testcase2: create cart")

    cart = {
        "items": [
            {
                "amount": 1,
                "product": product1
            }
        ]
    }

    # create a cart
    response = requests.post("http://localhost:8080/carts/api/carts", headers=headers, data=json.dumps(cart))

    assert response.status_code == 200, f"{response.content}"

    cart = response.json()

    pprint.pprint(cart)

    input("Press any key to continue...")

    print("Testcase 3: query the created cart")

    response = requests.get(f"http://localhost:8080/carts/api/carts/{cart['id']}")

    assert response.status_code == 200, f"{response.content}"

    cart2 = response.json()

    pprint.pprint(cart2)

    assert str(cart) == str(cart2), f"{cart}, {cart2}"

    cart = cart2

    input("Press any key to continue...")

    print("Testcase 4: add some items")

    # add some item
    item = {
        "amount": 2,
        "product": product2
    }

    response = requests.post(f"http://localhost:8080/carts/api/carts/{cart['id']}",
                             headers=headers, data=json.dumps(item))

    assert response.status_code == 200, f"{response.content}"

    cart = response.json()

    pprint.pprint(cart)

    input("Press any key to continue...")

    print("Testcase 5: calculate total price")

    total = product1['price'] + product2['price'] * 2

    response = requests.get(f"http://localhost:8080/carts/api/carts/{cart['id']}/total")

    assert response.status_code == 200, f"{response.content}"

    assert total == float(response.text), f"{total}, {response.text}"

    print(f"total: {total}")

    input("Press any key to continue...")

    print("Testcase 6: create an order")

    response = requests.post("http://localhost:8080/orders/api/orders", headers=headers, data=json.dumps(cart))

    assert response.status_code == 200, f"{response.content}"

    order = response.json()

    pprint.pprint(order)

    input("Press any key to continue...")

    print("Testcase 7: check waybill")

    # check waybill
    response = requests.get("http://localhost:8080/waybills/api/waybills")

    assert response.status_code == 200, f"{response.content}"

    waybills = response.json()

    pprint.pprint(waybills)

    assert any(waybill['order_id'] == order['id'] for waybill in waybills), waybills
