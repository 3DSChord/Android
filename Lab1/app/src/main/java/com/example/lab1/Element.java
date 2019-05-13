package com.example.lab1;

    public class Element {
        /* String array of words for tens Names */
        private static final String[] tensNames =
                {
                        "", "", "TWENTY", "THIRTY", "FORTY", "FIFTY", "SIXTY", "SEVENTY",
                        "EIGHTY", "NINETY"
                };

        /* String array of words for ones Names */
        private static final String[] onesNames =
                {
                        "", "ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT",
                        "NINE", "TEN", "ELEVEN", "TWELVE", "THIRTEEN", "FOURTEEN", "FIFTEEN",
                        "SIXTEEN", "SEVENTEEN", "EIGHTEEN", "NINETEEN"
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
                result.append(evaluate(thousands) + " THOUSAND ");
            }

            if (hundreds > 0)
            {
                result.append(evaluate(hundreds) + " HUNDRED ");
            }

            if (temp != 0)
            {
                if (number >= 100)
                {
                    result.append("AND ");
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
                result.append(" ZERO ");
            }

            return result.toString();
        }
    }

