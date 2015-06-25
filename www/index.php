<?php include "core/sql.php"; ?>


    <html>
        <head>

        </head>
        <body>
            <?php
                $sql = new SQL();
                $sql -> connect();
                $sql -> get_scripts("scripts");
                $sql -> disconnect();
            ?>
        </body>
    </html>