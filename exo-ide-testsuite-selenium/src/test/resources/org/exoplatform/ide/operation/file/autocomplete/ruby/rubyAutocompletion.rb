num = 121
MDA = 1.2
$cl = "Global variable"
class MyClass
  
  def initialize(x)
    @i = x
    @@ins = 0
  end
  
  def get
    @i
  end
  
  def set(x)
    @i = x
  end
  
  def hello
    h = "Hello"
    @i.times do
      print h, "\n"
    end    
    w = "World"
    print w
    
  end
  
end

g = MyClass.new(5)


