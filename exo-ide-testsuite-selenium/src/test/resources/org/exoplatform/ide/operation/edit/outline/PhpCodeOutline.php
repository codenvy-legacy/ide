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
    $a = (b() + 4);
    
    $t0 = False; // logical type
    $t1 = true; // logical type
    
    $t2 = -1;  // integer type
    $t3 = 01011; // octal integer
    $t4 = 0xa34; // hexadecimal integer

    $t5 = 1.2e-3;    # double number
    $t6 = -123.45;    # double number
    
    $t7 = "var $a plus {$arr[2]}";
    $t8 = 'var $a plus {$arr[2]}';
    
    $t9 = NULL;
    
    $t10 = new SimpleXMLElement;  // object
    $t11 = new \my\name\MyClass();  // object from namespace
    $t12 = new \Exception('error'); // instantiates global class Exception
     
      
    /* Access to global classes, functions and constants from within a namespace */
  $t13 = \strlen('hi'); // calls global function strlen
  static $t14 = \INI_ALL; // accesses global constant INI_ALL
    
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

         protected final function loadPageXML(UnderflowException $filename,
                                               array $merge,
                                              ArrayObject $x=0, $y=3)
         {
            $state = $row['c'] == 1;
            $sql = "multiline string
line2 is special - it'll interpolate variables like $state and method calls
{$this->cache->add($key, 5)} and maybe \"more\"

line5";
            $sql = 'multiline string
single quoting means no \'interpolation\' like "$start" or method call
{$this->cache->add($key, 5)} will happen

line5';
            $bitpattern = 1 << 2;
            $sql = <<< EOSQL
                SELECT attribute, element, value
                FROM attribute_values
                WHERE dimension = ?
EOSQL;
            $this->lr_cache->add($key, self::range($row['lft'], $row['rgt']));
            $composite_string = <<<EOSTRING
some lines here
EOSTRING
. 'something extra';
        }

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

    switch( $type ) {             
       case "r3core_GenerateTargetEvent":
           for($i=0; $i<=$this->method(); $i++) {
               echo 'Syntax "highlighting"';
           }
                
           try {
               foreach($array xor $loader->parse_fn($filename) as $key => $value) {
               }
           } catch( Exception $e ) {
               /** restore the backup
               */
               $this->loadAll($tmp, $event, true);
           }

           break;
    }
    
    if($something==true):
      $test = 'something';
      $q1 = 1;     
    endif;
    
    for($i=0;$i<2000;$i++):
      echo $i;
      $k = $k + 1;
    endfor;
    
    foreach(array('one','two') as $key => $value):
      echo $number . ' = '. $key;
    endforeach;

    ?>

    <r3:cphp>
        php("works", $here, 2);
    </r3:cphp>

    <r4:cphp>
    class foo {
        // a comment
        var $a;
        var $b;
    };
    </r4:cphp>

<?php echo "<script>
    var foo = 'bar';
    </script>
    <!-- comment -->"; ?>

<?php
define('TEST', "");
echo $GLOBALS['test'];
echo __LINE__;
public function test($a,
                    $b = array(1)){
    return false;
}
/**
 * @deprecated
 */
class Foo extends ReflactionClass implements Foo\IBar{
    const E_USER1_ERROR = 45;
    var $test;
    &$c = 3;
    public static $my_static = 'foo';
    private function test($test){
       return $this->test;
    }
     static function foo(){
       return false;
    }
}

$text =<<<HEREDOC
Hello, World!
HEREDOC

    /**
     * Sample function
     * @throws Exception
     */
    function foo($a){
        if (func_num_args() != 1) {
           throw new Exception ("Illegal number of arguments!");
        }
        // TODO: do something
        $args = func_get_args();
        print "{$args[0]}\n";
    }
    
    
  /* compile time constant http://php.net/manual/en/reserved.php */  
    __FILE__
    
    
    /* Unqualified name */
  foo(); // resolves to function Foo\Bar\foo
  foo::staticmethod(); // resolves to class Foo\Bar\foo, method staticmethod
  echo FOO; // resolves to constant Foo\Bar\FOO
  
  /* Qualified name */
  subnamespace\foo(); // resolves to function Foo\Bar\subnamespace\foo
  subnamespace\foo::staticmethod(); // resolves to class Foo\Bar\subnamespace\foo,
                                    // method staticmethod
  echo subnamespace\FOO; // resolves to constant Foo\Bar\subnamespace\FOO
                                    
  /* Fully qualified name */
  \Foo\Bar\foo(); // resolves to function Foo\Bar\foo
  \Foo\Bar\foo::staticmethod(); // resolves to class Foo\Bar\foo, method staticmethod
  echo \Foo\Bar\FOO; // resolves to constant Foo\Bar\FOO
  
?>

</body>
</html>