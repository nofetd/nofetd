import sqlite3
import printdb
import os
import sys


def update_products_table(cur, product_id, quantity):
    sql_update_product = ''' UPDATE Products SET quantity = ? WHERE id=?'''
    cur.execute(sql_update_product, (quantity, product_id,))


def create_activities(cur, a, b, c, d):
    sql_activities = ''' INSERT INTO Activities(product_id,quantity,activator_id,date) VALUES (?,?,?,?)'''
    cur.execute(sql_activities, (a, b, c, d,))


def main(args):
    con = sqlite3.connect('moncafe.db')
    with con:
        cur = con.cursor()
        action_file = args[2]
        with open(action_file, 'r') as r:
            for line in r:
                legal = 0
                x = line.split(", ")
                product_id = x[0]
                quantity = x[1]     # make it string
                cur.execute('SELECT quantity FROM Products WHERE Products.id=?', (product_id,))
                row = cur.fetchone()
                if int(quantity) < 0:   # sale activity
                    if row[0] + int(quantity) >= 0:
                        legal = 1
                elif int(quantity) > 0:   # supply arrival
                    legal = 1

                if legal == 1:
                    line = line.split(", ")
                    create_activities(cur, line[0], line[1], line[2], line[3])
                    i = int(row[0]) + int(quantity)
                    update_products_table(cur, product_id, str(i))

    printdb.printdb()


if __name__ == '__main__':
    main(sys.argv)


