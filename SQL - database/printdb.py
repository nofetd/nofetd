import sqlite3
import os


def printdb():
    con = sqlite3.connect('moncafe.db')
    with con:
        cur = con.cursor()
        print("Activities")
        cur.execute("SELECT * from Activities ORDER BY date")
        rows1 = cur.fetchall()
        for row in rows1:
            print(row)

        print("Coffee stands")
        cur.execute("SELECT * from Coffee_stands ORDER BY id")
        rows2 = cur.fetchall()
        for row in rows2:
            print(row)

        print("Employees")
        cur.execute("SELECT * from Employees ORDER BY id")
        rows3 = cur.fetchall()
        for row in rows3:
            print(row)

        print("Products")
        cur.execute("SELECT * from Products ORDER BY id")
        rows4 = cur.fetchall()
        for row in rows4:
            print(row)

        print("Suppliers")
        cur.execute("SELECT * from Suppliers ORDER BY id")
        rows5 = cur.fetchall()
        for row in rows5:
            print(row)

        print("Employees report")
        cur.execute(" SELECT Employees.id, Employees.name, "
                    " Employees.salary, Coffee_stands.location, ABS(SUM(Activities.quantity*Products.price))"
                    " FROM Employees LEFT JOIN Coffee_stands on Employees.coffee_stand = Coffee_stands.id"
                    " LEFT JOIN Activities on Activities.activator_id = Employees.id"
                    " LEFT JOIN Products on Activities.product_id = Products.id"
                    " Group by Employees.id"
                    " ORDER BY Employees.name")

        row6 = cur.fetchall()
        for row in row6:
            i = -1
            if row[4] is None:
                i = 0
            else:
                i = row[4]
            toprint = row[1] + " " + str(row[2]) + " " + row[3] + " " + str(i)
            print(toprint)

        print("Activities report")
        cur.execute(" SELECT Activities.date, Products.description, Activities.quantity, Employees.name, Suppliers.name"
                    " FROM Activities LEFT JOIN Products on Activities.product_id = Products.id"
                    " LEFT JOIN Employees on Employees.id = Activities.activator_id"
                    " LEFT JOIN Suppliers on Suppliers.id = Activities.activator_id"
                    " ORDER BY Activities.date")
        row7 = cur.fetchall()
        for row in row7:
            print(row)


if __name__ == '__main__':
    printdb()