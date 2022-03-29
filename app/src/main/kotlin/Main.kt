fun main() {
    val foo = FooProxy()
    println("origin:")
    Foo().doHeavyWork(10)
    println("proxy 10:")
    foo.doHeavyWork(10)
    println("proxy Int.Max:")
    foo.doHeavyWork()
    println("Program ended")
}