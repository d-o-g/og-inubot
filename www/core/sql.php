<?php
/**
 * Created by IntelliJ IDEA.
 * User: Septron
 * Date: 6/24/2015
 * Time: 9:21 PM
 */

include "config.php";

class SQL extends Config {

    protected $database;
    protected $username;
    protected $password;
    protected $host;

    public $connection;
    public $data;

    private $query;

    function __construct() {
        $this -> connection = NULL;
        $this -> query = NULL;
        $this -> data = NULL;

        $config = new Config();
        $this -> database = $config -> database;
        $this -> username = $config -> username;
        $this -> password = $config -> password;
        $this -> host = $config -> host;
        $config = NULL;
    }

    function connect() {
        $this -> connection = mysqli_connect($this -> host, $this -> username, $this -> password);
        mysqli_select_db($this -> connection, $this -> database);
        return $this -> connection;
    }

    function disconnect() {
        $this -> connection = NULL;
        $this -> query = NULL;
        $this -> data = NULL;

        $this -> database = NULL;
        $this -> username = NULL;
        $this -> password = NULL;
        $this -> host = NULL;
    }

    function insert($table, $values) {
        $i = NULL;

        $this -> query = 'INSERT INTO ' . $table . ' VALUES (';
        $i = 0;
        while($values[$i]["val"] != NULL && $values[$i]["type"] != NULL)    {
            if($values[$i]["type"] == "char")   {
                $this -> query .= "'";
                $this -> query .= $values[$i]["val"];
                $this -> query .= "'";
            }
            else if($values[$i]["type"] == 'int')   {
                $this -> query .= $values[$i]["val"];
            }
            $i++;
            if($values[$i]["val"] != NULL)  {
                $this -> query .= ',';
            }
        }
        $this -> query .= ')';
        mysqli_query($this -> connection, $this -> query);
        return $this -> query;
    }

    function get_scripts($table) {
        $this -> query = 'SELECT * FROM ' . $table . ';';
        if ($result = mysqli_query($this -> connection, $this -> query)) {
            while($row = $result -> fetch_array()) {
                echo "ID: " . $row["ID"]. " - Name: " . $row["name"]. " Description: " . $row["description"]. "<br>";
            }
        } else {
            echo "Failed query!";
        }
    }
}

 ?>