=begin
 This is about ClassName.
=end

TOPLEVEL_CONSTANT = "Test"

@@n2 = [    
     :"blocks with params",
       "indentation"
]
$global
@n1
string
@@class_variable
CLASS_CONSTANT

class ClassName < SuperClass::SomeOther::NameSpace
  include Comparable
  
  CLASS_CONSTANT = 123
  $global = 'around the world'
  $global1 = cons.new
  @@class_variable = /[a-z0-9!]?/i   # example of regexp
  @field = value

  def initialize(value)
    @field = value
    @@class_variable = "some string"
    @@char = ?a
    METHOD_CONSTANT = "Test"
  end
  
  $myFile = File.new("example.txt", "w")   
  
  scale = "C"
  case scale
    when "C", "c"
    f = temp
    # Strings
    @str4 = "string #{ with(:ruby).inside } ex"      #double quotes with ruby
  else
    raise argumentError, "Wrong Data", caller[0]
  end
  
  loop do  
    print list[i]
    i += 1
    @str4 = "The answer is #{factorial(3) * 7}, and #@classVar, so." #double quotes with @classVar       
  end
      
  # Numbers
  @n1 = 123.45    #floating point number
  @@n2 = 1.2e-3    #floating point number
end

a = ClassName.new

class TC_MyTest < Test::Unit::TestCase
  # TASK: refactor
   def foo parameter1, parameter2
     abstract_method()
     @@class_variable = {:name => 'foo'}
     @field = 0b01011   #binary integer
     return bar(local) + parameter
   end

   CLASS_CONSTANT = 0xffff    #hexadecimal integer
   a = 0377      #octal integer
end

module TestModule
  def method
    @@t = true
    $global = "test"
  end
  A = :symbol #Integer corresponding identifiers, variable names, and operators.
  ascii1 = ?a        #ASCII code for character `a'(97)
  @field
end