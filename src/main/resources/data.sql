INSERT INTO permissions(name, description)
VALUES
    ('LOGIN',                   'permesso di accesso'),
    ('LOGOUT',                  'permesso di uscita'),
    ('READ_CUSTOMER',           'permesso di lettura della lista dei customers'),
    ('READ_CUSTOMER_DETAILS',   'permesso di lettura dei dettagli dei customers'),
    ('ADD_CUSTOMER',            'permesso di aggiunta customers'),
    ('EDIT_CUSTOMER',           'permesso di modifica customers (es.: abilitazione)'),
    ('DELETE_CUSTOMER',         'permesso di cancellazione customers'),
    ('DOWNLOAD_CUSTOMER',       'permesso di download dei dai del customers'),
    ('READ_PLATFORMS',          'permesso di leggere la lista delle piattaforme'),
    ('READ_PLATFORM_DETAILS',   'permesso di leggere i dettagli delle platforms'),
    ('EDIT_PLATFORM',           'permesso di modificare le platforms'),
    ('DELETE_PLATFORM',         'permesso di eliminare le platforms'),
    ('DOWNLOAD_PLATFORM',       'permesso di scaricare i dati delle platforms')
;

INSERT INTO platforms(id, name, open)
VALUES
    (1, 'Test', '2023-11-20 14:35:00'),
    (2, 'Mobile Agent Diagnostic Portal', '2023-11-20 14:35:00')
;

