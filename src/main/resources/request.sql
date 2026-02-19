SELECT i.id, i.customer_name, SUM(il.quantity * il.unit_price) FROM invoice i
    JOIN invoice_line il ON i.id = il.invoice_id
GROUP BY i.id, i.customer_name
ORDER BY i.id;

SELECT i.customer_name, i.status, SUM(il.quantity * il.unit_price) FROM invoice i
JOIN invoice_line il ON i.id = il.invoice_id
WHERE i.status = 'CONFIRMED' OR i.status = 'PAID'
GROUP BY i.status, i.customer_name;

SELECT  i.status, SUM(il.quantity * il.unit_price) FROM invoice i
JOIN invoice_line il ON i.id = il.invoice_id
GROUP BY i.status;

SELECT SUM((case when i.status = 'PAID' THEN il.quantity * il.unit_price else 0 end)) as paid_amount,
       SUM((case when i.status = 'CONFIRMED' THEN il.quantity * il.unit_price else 0 end)) as confirmed_amount,
       SUM((case when i.status = 'DRAFT' THEN il.quantity * il.unit_price else 0 end)) as draft_amount
FROM invoice i
JOIN invoice_line il ON i.id = il.invoice_id;


SELECT SUM(
    CASE
        WHEN i.status = 'PAID' THEN  il.quantity * il.unit_price
       WHEN i.status = 'CONFIRMED' THEN (il.quantity * il.unit_price) / 2
       ELSE 0
    END
) AS chiffre_d_affaire FROM invoice i
JOIN invoice_line il ON i.id = il.invoice_id;

CREATE TABLE tax_config (
                            id SERIAL PRIMARY KEY,
                            label VARCHAR NOT NULL,
                            rate NUMERIC(5,2) NOT NULL
);

INSERT INTO tax_config (id, label, rate) VALUES
    (1, 'TVA STANDARD', 20);

SELECT * FROM tax_config;

SELECT i.id,
       SUM(il.quantity * il.unit_price) AS HT,
       t.rate * SUM(il.quantity * il.unit_price)  / 100 AS TVA,
       SUM(il.quantity * il.unit_price) + (( t.rate * SUM(il.quantity * il.unit_price) ) / 100) AS TTC
FROM invoice i
JOIN invoice_line il ON i.id = il.invoice_id
JOIN tax_config t ON 1 = 1
GROUP BY i.id, t.rate
ORDER BY i.id;

SELECT SUM (
    CASE
            WHEN i.status = 'PAID' THEN (il.quantity * il.unit_price) + (( t.rate * (il.quantity * il.unit_price) ) / 100)
            WHEN i.status = 'CONFIRMED' THEN  ((il.quantity * il.unit_price) + (( t.rate * (il.quantity * il.unit_price) ) / 100) ) / 2
            ELSE 0
       END
       ) AS chiffre_d_affaire_TTC
FROM invoice i
         JOIN invoice_line il ON i.id = il.invoice_id
         JOIN tax_config t ON 1 = 1
WHERE i.status != 'DRAFT'
GROUP BY  t.rate