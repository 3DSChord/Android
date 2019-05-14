package com.example.lab1;

    public class Element {
        /* String array of words for thousends Names */
        private static final String[] thousendsNames =
                {
                        "ТЫСЯЧ", "ТЫСЯЧА", "ТЫСЯЧИ", "ТЫСЯЧИ", "ТЫСЯЧИ", "ТЫСЯЧ", "ТЫСЯЧ", "ТЫСЯЧ",
                        "ТЫСЯЧ", "ТЫСЯЧ"
                };

        /* String array of words for hundreds Names */
        private static final String[] hundredsNames =
                {
                        "", "СТО", "ДВЕСТИ", "ТРИСТА", "ЧЕТЫРЕСТА", "ПЯТЬСОТ", "ШЕСТЬСОТ", "СЕМЬСОТ",
                        "ВОСЕМЬСОТ", "ДЕВЯТЬСОТ"
                };

        /* String array of words for tens Names */
        private static final String[] tensNames =
                {
                        "", "", "ДВАДЦАТЬ", "ТРИДЦАТЬ", "СОРОК", "ПЯТЬДЕСЯТ", "ШЕСТЬДЕСЯТ", "СЕМЬДЕСЯТ",
                        "ВОСЕМЬДЕСЯТ", "ДЕВЯНОСТО"
                };

        /* String array of words for ones Names */
        private static final String[] onesNames =
                {
                        "", "ОДИН", "ДВА", "ТРИ", "ЧЕТЫРЕ", "ПЯТЬ", "ШЕСТЬ", "СЕМЬ", "ВОСЕМЬ",
                        "ДЕВЯТЬ", "ДЕСЯТЬ", "ОДИННАДЦАТЬ", "ДВЕНАДЦАТЬ", "ТРИНАДЦАТЬ", "ЧЕТЫРНАДЦАТЬ", "ПЯТНАДЦАТЬ",
                        "ШЕСТНАДЦАТЬ", "СЕМНАДЦАТЬ", "ВОСЕМНАДЦАТЬ", "ДЕВЯТНАДЦАТЬ"
                };
        private int val;

        public Element(int val) {
            this.val = val;
        }

        public int getVal() { return this.val;}
        public String getStr() {return evaluate(this.val);}
        public void setVal(int val) {
            this.val = val;
        }

        private String evaluate(long number)
        {
            long temp = number;

            long crore = temp / 10000000;
            temp %= 10000000;

            long lakh = temp / 100000;
            temp %= 100000;

            long thousands = temp / 1000;
            temp %= 1000;

            long hundreds = temp / 100;
            temp %= 100;

            StringBuffer result = new StringBuffer(30);

            if (crore > 0)
            {
                result.append(evaluate(crore) + " CRORE ");
            }

            if (lakh > 0)
            {
                result.append(evaluate(lakh) + " LAKH ");
            }

            if (thousands > 0)
            {
                int itemp = ( int ) thousands % 100;
                int jtemp = ( int ) thousands % 10;
                String istr = evaluate(thousands);
                if ((itemp < 11) || (itemp > 20)) {
                    if (jtemp == 1)
                        istr = istr.replace("ОДИН", "ОДНА");
                    if (jtemp == 2)
                        istr = istr.replace("ДВА", "ДВЕ");
                    istr = istr + " " + thousendsNames[jtemp] + " ";
                } else {
                    istr = istr + " ТЫСЯЧ ";
                }
                result.append(istr);
            }

            if (hundreds > 0)
            {
                result.append(hundredsNames[( int ) hundreds] + " ");
            }

            if (temp != 0)
            {
                if (number >= 100)
                {
                    result.append(" ");
                }

                if ((0 < temp) && (temp <= 19))
                {
                    result.append(onesNames[( int ) temp]);
                }
                else
                {
                    long tens = temp / 10;
                    long ones = temp % 10;
                    result.append(tensNames[( int ) tens] + " ");
                    result.append(onesNames[( int ) ones]);
                }
            }

            if ((result
                    .toString()).trim()
                    .equals(""))
            {
                result.append(" НОЛЬ ");
            }

            return result.toString();
        }
    }

