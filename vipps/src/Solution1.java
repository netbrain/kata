class Solution1 {
    public String solution(String S) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < S.length(); i++) {
            int charCode = (int)S.charAt(i);
            if (charCode >= 48 && charCode <= 57){
                output.append((char) charCode);
            }
        }

        int length = output.length();
        int offset = 0;
        for(int i = 3; i < length; i += 3){
            if (length - i < 2){
                offset--;
            }
            output.insert(i+offset,'-');
            offset++;
        }

        return output.toString();
    }
}