<?php

/**
 * Created by IntelliJ IDEA.
 * User: Septron
 * Date: 6/24/2015
 * Time: 9:26 PM
 * @property string database
 */

 class Config {

     protected $database;
     protected $username;
     protected $password;
     protected $host;

     function __construct() {
         $this -> database = "inu";
         $this -> username = "root";
         $this -> password = "";
         $this -> host = "127.0.0.1";
     }

 }