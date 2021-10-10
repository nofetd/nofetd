import sqlite3
import os
import sys
import action
import printdb


def create_employee(cur, employee):
    sql_employees = ''' INSERT INTO Employees(id,name,salary,coffee_stand) VALUES (?,?,?,?)'''
    cur.execute(sql_employees, employee)


def create_suppliers(cur, supplier):
    sql_suppliers = ''' INSERT INTO Suppliers(id,name,contact_information) VALUES (?,?,?)'''
    cur.execute(sql_suppliers, supplier)


def create_products(cur, product):
    sql_products = ''' INSERT INTO Products(id,description,price,quantity) VALUES (?,?,?,0)'''
    cur.execute(sql_products, product)


def create_coffee_stands(cur, coffee_stand):
    sql_coffee_stand = ''' INSERT INTO Coffee_stands(id,location,number_of_employees) VALUES (?,?,?)'''
    cur.execute(sql_coffee_stand, coffee_stand)


def main(args):
    DBExist = os.path.isfile('moncafe.db')
    if DBExist:
        os.remove('moncafe.db')

    config_file = args[1]
    con = sqlite3.connect('moncafe.db')
    with con:
        cur = con.cursor()
        cur.execute(""" CREATE TABLE IF NOT EXISTS Employees(
        id INTEGER PRIMARY KEY,
        name TEXT NOT NULL,
        salary REAL NOT NULL,
        coffee_stand INTEGER REFERENCES Coffee_stand(id)
        ); """)

        cur.execute(""" CREATE TABLE IF NOT EXISTS Suppliers(
        id INTEGER PRIMARY KEY,
        name TEXT NOT NULL,
        contact_information TEXT
        ); """)

        cur.execute(""" CREATE TABLE IF NOT EXISTS Products(
        id INTEGER PRIMARY KEY,
        description TEXT NOT NULL,
        price REAL NOT NULL,
        quantity INTEGER NOT NULL
        ); """)

        cur.execute(""" CREATE TABLE IF NOT EXISTS Coffee_stands(
        id INTEGER PRIMARY KEY,
        location TEXT NOT NULL,
        number_of_employees INTEGER
        ); """)

        cur.execute(""" CREATE TABLE IF NOT EXISTS Activities(
        product_id INTEGER REFERENCES Product(id),
        quantity INTEGER NOT NULL,
        activator_id INTEGER NOT NULL,
        date DATE NOT NULL
        ); """)

        with open(config_file) as r:
            lines = r.readlines()
            for line in lines:
                first = line[0]
                line = line[3:]
                words = line.split(", ")
                if first == 'E':
                    create_employee(cur, words)
                elif first == 'S':
                    create_suppliers(cur, words)
                elif first == 'P':
                    create_products(cur, words)
                elif first == 'C':
                    create_coffee_stands(cur, words)


if __name__ == '__main__':
    main(sys.argv)























