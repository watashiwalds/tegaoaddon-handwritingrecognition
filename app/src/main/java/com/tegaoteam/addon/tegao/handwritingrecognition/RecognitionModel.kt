package com.tegaoteam.addon.tegao.handwritingrecognition

class RecognitionModel {
    companion object {
        private val randomChars = listOf<Char>('漢','字','梵','語','千','文','鬘','唐','聖','照','権','実','鏡','弘','仁','真','名','仮','平','万','葉','子','供','煙','草','天')
        private val randomNumbers = listOf<Int>(0,1,2,3,4,5,6,7,8,9,10)
        fun getSomeRandomChars(): MutableList<String> {
            val result = mutableListOf<String>()
            for (i in 0..randomNumbers.random()) result.add(randomChars.random().toString())
            return result
        }
    }
}