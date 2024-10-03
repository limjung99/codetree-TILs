import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

class Node{
    String owner;
    int pos;
    public Node(String owner,int pos){
        this.owner = owner;
        this.pos = pos;
    }
}

public class Main {
    static int L,Q;
    static int lastT; // 마지막 연산이 발생한 t
    static Map<String, List<Node>> map = new HashMap<>(); // 레일에 남아있는 초밥 리스트
    static Map<String,Integer> count = new HashMap<>();
    static Map<String,Integer> ownerPos = new HashMap<>();

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        L = Integer.parseInt(st.nextToken());
        Q = Integer.parseInt(st.nextToken());
        StringBuilder sb = new StringBuilder();

        for(int i=0;i<Q;i++){
            st = new StringTokenizer(br.readLine());
            int op = Integer.parseInt(st.nextToken());
            int t = Integer.parseInt(st.nextToken());
            computePos(t);
//            debug(t);
            if(op == 100){
                int x = Integer.parseInt(st.nextToken());
                String name = st.nextToken();
                makeSushi(x,name);
            } else if(op == 200){
                int x = Integer.parseInt(st.nextToken());
                String name = st.nextToken();
                int n = Integer.parseInt(st.nextToken());
                customerIn(x,name,n);
            } else if(op == 300){
                photo(sb);
            }

            computePos(t);
        }

        System.out.println(sb);
    }

    static void debug(int t){
        System.out.println("after compute :"+t);
        System.out.println("owner info ------------");

        for(Map.Entry<String,Integer> info : ownerPos.entrySet()){
            String key = info.getKey();
            System.out.println(key);
            System.out.println("pos :"+info.getValue()+" cnt:"+count.get(key));
        }

        System.out.println("-----------------------");
        System.out.println("sushi information");
        for(Map.Entry<String, List<Node>> entry : map.entrySet()){
            String name = entry.getKey();
            List<Node> list = entry.getValue();
            System.out.println("Name : "+name);
            for(Node n : list){
                System.out.print(n.pos+" ");
            }
            System.out.println();
        }
        System.out.println("##################");
    }

    static void computePos(int t){
        // lastT 로부터 변위를 통해 모든 손님에 대해 초밥의 현재위치 재계산
        int delta = t - lastT;

        for(Map.Entry<String,List<Node>> entry : map.entrySet()){
            String name = entry.getKey();
            List<Node> list = entry.getValue();
            List<Node> newList = new ArrayList<>();
            boolean ownerExists = false;

            if(ownerPos.containsKey(name)){
                ownerExists = true;
            }

            for(Node n : list){
                int nextPos = (n.pos + delta)%L;
                int turnAround = delta/L;

                if(!ownerExists){
                   // 아직 주인이 도착하지 않음 -> 위치만 업데이트 후 종료
                    n.pos = nextPos;
                    newList.add(n);
                    continue;
                }

                int ownerPosition = ownerPos.get(name);
                if((n.pos <= ownerPosition && ownerPosition <= nextPos)
                        || (ownerPosition <= n.pos && ownerPosition <= nextPos)
                        || turnAround >= 1 ){
                    // 초밥 먹음
                    int cnt = count.get(name);
                    cnt--;
                    count.put(name,cnt);
                } else {
                    // 초밥 안먹음
                    newList.add(n);
                }

                n.pos = nextPos;
            }

            if(ownerExists){
                int cnt = count.get(name);
                if(cnt <= 0){
                    ownerPos.remove(name);
                }
            }

            map.put(name,newList);
        }

        // 마지막 계산시간 할당
        lastT = t;
    }

    static void makeSushi(int x,String name){
        // 초밥 만들기
        if(map.containsKey(name)){
            map.get(name).add(new Node(name,x));
        } else {
            map.put(name,new ArrayList<>());
            map.get(name).add(new Node(name,x));
        }
    }

    static void customerIn(int x,String name,int n){
        count.put(name,n);
        ownerPos.put(name,x);
    }

    static void photo(StringBuilder sb){
        // map 자료구조 탐색 && StringBuilder update
        int numOfPerson = ownerPos.size();
        int leftSushi = 0;

        for(Map.Entry<String,List<Node>> entry : map.entrySet()){
            leftSushi += entry.getValue().size();
        }

        sb.append(numOfPerson).append(" ").append(leftSushi).append("\n");
    }
}