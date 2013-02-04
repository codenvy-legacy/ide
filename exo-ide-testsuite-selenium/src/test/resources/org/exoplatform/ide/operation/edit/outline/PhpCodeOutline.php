<?php
  namespace A;
  namespace my\name;
  
  interface test extends a {
    static function test_interface_static($var1, $var2);
    function test_interface($var1);
    const b = 'Interface constant';
  }
    
    // Prints: Interface constant
    echo test::b;
    
    ?>
<?php echo 'text output by';
    ?>
<?xml version='1.0' encoding='UTF-8' standalone='yes'?>
<html>
  <head>
    <script type="text/javascript">
      var regex = /foo|bar/i;
      
      function x(a) {
        var y = 44.4;
      }
    </script>
  </head>
  <body>
    <?php    
    define("CONSTANT_EX",
           "text");
    
    const a = 1;
    
    define("CONSTANT_EX", false);
    
    global $g;
    
    CONSTANT_EX;
    
    self::range($row['lft'], $row['rgt']);
    
    $t0 = False; // logical type
    
    $t2 = -1;  // integer type
    
    $t5 = 1.2e-3;    # double number
    
    $t7 = "var $a plus {$arr[2]}";
    
    $t9 = NULL;
    
    $t10 = new SimpleXMLElement;  // object
    
    
    /* Access to global classes, functions and constants from within a namespace */
    $t15 = array(1=>1, 'a'=>2, 3);  // array
    
    self::$var;
    $parent = self::range($max + 1, $max + 1);
    $row[$attributes()][$attribute_name] = $attribute_value;
    abstract class test extends foo implements test
    {
      private function domainObjectBuilder($var2) {
        $test = 1;
        define("CONSTANT_EX2", False);
        return $this->use_domain_object_builder
          ? $this->domain()->objectBuilder()
          : null;
      }
      
      const MYCONST = 'some string';
      
      define("CONSTANT_EX1", false);
      
      $g[MYCONST] = 4;
      // this is a single-line C++-style comment
      # this is a single-line shell-style comment
      var $a = __FILE__;
      private $a = __FILE__;
      protected static $b = 'test';
      static $s;  // warning: predefined function non-call
      function mike($var);
      func($b,$c);        
      mike(A::func(param));
      
      /*
      echo 'This is a test';
      */
      function makecoffee_error($types = array(), $coffeeMaker = NULL) {
        foreach (r3_Domain::names() as $domain_name) {
          $placeholders = 'distance LIKE '
            . implode(array_fill(1, $num_distances, '?'),
                      ' OR distance LIKE ');
          
        }
        return $this->target*$this->trans+myfunc(__METHOD__);
      }
    }
      ?>
  </body>
</html>
