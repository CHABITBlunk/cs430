import csv
from datetime import datetime


def sql_date(date_str):
    return datetime.strptime(date_str.strip(), "%m/%d/%Y").strftime("%Y-%m-%d")


with open("Author.csv") as f:
    reader = csv.reader(f)
    for row in reader:
        author_id = int(row[0])
        first = row[1]
        last = row[2]
        phones = row[3:]

        print(f"insert into author values ({author_id}, '{first}', '{last}');")

        for i in range(0, len(phones), 2):
            if i + 1 >= len(phones) or phones[i] == "None":
                continue
            p_number = phones[i]
            type_code = phones[i + 1].strip("()").lower()
            phone_type = {"c": "cell", "h": "home", "o": "office"}.get(
                type_code, "unknown"
            )

            print(f"insert into phone values ('{p_number}', '{phone_type}');")
            print(
                f"insert into phone_owner values ('{p_number}', 'author', {author_id});"
            )

with open("Publisher.csv") as f:
    reader = csv.reader(f)
    for row in reader:
        publisher_id = int(row[0])
        first = row[1]
        phones = row[2:]

        print(f"insert into publisher values ({publisher_id}, '{first}');")

        for i in range(0, len(phones), 2):
            if i + 1 >= len(phones) or phones[i] == "None":
                continue
            p_number = phones[i]
            type_code = phones[i + 1].strip("()").lower()
            phone_type = {"c": "cell", "h": "home", "o": "office"}.get(
                type_code, "unknown"
            )

            print(f"insert into phone values ('{p_number}', '{phone_type}');")
            print(
                f"insert into phone_owner values ('{p_number}', 'publisher', {publisher_id});"
            )

with open("Book.csv") as f:
    current_isbn = None
    for line in f:
        line = line.rstrip("\n")
        if not line.strip():
            continue

        if not line.startswith("  "):
            parts = line.split(",")
            if len(parts) < 7:
                continue
            isbn = parts[0]
            title = parts[4].replace("'", "''")
            pub_id = int(parts[5])
            pub_date = sql_date(parts[6])

            print(
                f"insert into book (isbn, title, pub_id, year_published) "
                f"values ('{isbn}', '{title}', {pub_id}, '{pub_date}');"
            )

            current_isbn = isbn
        else:
            pass

with open("Book.csv") as f:
    current_isbn = None
    for line in f:
        line = line.rstrip("\n")
        if not line.strip():
            continue

        if not line.startswith("  "):
            parts = line.split(",")
            if len(parts) < 7:
                continue
            current_isbn = parts[0]
        else:
            author_ids = line.strip().split(",")
            for author_id in author_ids:
                author_id = author_id.strip()
                if author_id:
                    print(
                        f"insert into book_author (isbn, author_id) values ('{current_isbn}', {author_id});"
                    )

with open("Members.csv") as f:
    reader = csv.reader(f)
    for row in reader:
        if not row or not row[0].strip().isdigit():
            continue
        member_id = int(row[0])
        first = row[1].strip().replace("'", "''")
        last = row[2].strip().replace("'", "''")
        dob = sql_date(row[4])
        print(f"insert into member values ({member_id}, {first}, {last}, {dob});")

with open("Members.csv") as f:
    current_member_id = None
    for line in f:
        line = line.strip()
        if not line:
            continue
        parts = [p.strip() for p in line.split(",")]

        if parts[0].isdigit():
            current_member_id = int(parts[0])
        else:
            if len(parts) < 2 or current_member_id is None:
                continue
            isbn = parts[0]
            checkout = sql_date(parts[1])
            if len(parts) >= 3 and parts[2]:
                checkin = sql_date(parts[2])
                print(
                    f"insert into borrowed (member_id, checkout_date, checkin_date) "
                    f"values ({current_member_id}, '{isbn}', '{checkout}', '{checkin}');"
                )
            else:
                print(
                    f"insert into borrowed (member_id, checkout_date, checkin_date) "
                    f"values ({current_member_id}, '{isbn}', '{checkout}', null);"
                )
