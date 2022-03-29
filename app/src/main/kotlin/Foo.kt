import com.github.xuankaicat.annotation.MyAspect
import com.github.xuankaicat.annotation.ComputingTime

@MyAspect
open class Foo {
    @ComputingTime
    open fun doHeavyWork(size: Int = Int.MAX_VALUE) {
        var v = 0L
        for (i in 0..size) {
            v += i
        }
        println("heavy work done, result is $v")
    }
}