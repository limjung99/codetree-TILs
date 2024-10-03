import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

class Sushi{
    String owner;
    int pos;
    public Sushi(String owner,int pos){
        this.owner = owner;
        this.pos = pos;
    }
}

class Owner{
    int pos;
    int cnt;
    public Owner(int pos,int cnt){
        this.pos = pos;
        this.cnt = cnt;
    }
}



public class Main {
    static int L,Q;
    static int lastT; // 마지막 연산이 발생한 t
    static Map<String, List<Sushi>> sushiMap = new HashMap<>(); // 레일에 남아있는 초밥 리스트
    static Map<String,Owner> owners = new HashMap<>();

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

        for(Map.Entry<String,Owner> ownerInfo : owners.entrySet()){
            String key = ownerInfo.getKey();
            Owner owner = ownerInfo.getValue();
            System.out.println(key);
            System.out.println("pos :"+owner.pos+" cnt:"+owner.cnt);
        }

        System.out.println("-----------------------");
        System.out.println("sushi information");
        for(Map.Entry<String, List<Sushi>> entry : sushiMap.entrySet()){
            String name = entry.getKey();
            List<Sushi> list = entry.getValue();
            System.out.println("Name : "+name);
            for(Sushi n : list){
                System.out.print(n.pos+" ");
            }
            System.out.println();
        }
        System.out.println("##################");
    }

    static void computePos(int t){
        // lastT 로부터 변위를 통해 모든 손님에 대해 초밥의 현재위치 재계산
        int delta = t - lastT;

        for(Map.Entry<String,List<Sushi>> entry : sushiMap.entrySet()){
            String name = entry.getKey();
            List<Sushi> list = entry.getValue();
            List<Sushi> newList = new ArrayList<>();
            boolean ownerExists = false;
            Owner owner = owners.get(name);

            if(owners.containsKey(name)){
                ownerExists = true;
            }

            for(Sushi n : list){
                int nextPos = (n.pos + delta)%L;
                int turnAround = delta/L;

                if(!ownerExists){
                   // 아직 주인이 도착하지 않음 -> 위치만 업데이트 후 종료
                    n.pos = nextPos;
                    newList.add(n);
                    continue;
                }

                int ownerPosition = owner.pos;
                if((n.pos <= ownerPosition && ownerPosition <= nextPos)
                        || (ownerPosition <= n.pos && ownerPosition <= nextPos)
                        || turnAround >= 1 ){
                    // 초밥 먹음
                    int cnt = owner.cnt;
                    cnt--;
                    owner.cnt = cnt;
                } else {
                    // 초밥 안먹음
                    newList.add(n);
                }

                n.pos = nextPos;
            }

            if(ownerExists){
                int cnt = owner.cnt;
                if(cnt <= 0){
                    owners.remove(name);
                }
            }

            sushiMap.put(name,newList);
        }

        // 마지막 계산시간 할당
        lastT = t;
    }

    static void makeSushi(int x,String name){
        // 초밥 만들기
        if(sushiMap.containsKey(name)){
            sushiMap.get(name).add(new Sushi(name,x));
        } else {
            sushiMap.put(name,new ArrayList<>());
            sushiMap.get(name).add(new Sushi(name,x));
        }
    }

    static void customerIn(int x,String name,int n){
        owners.put(name,new Owner(x,n));
    }

    static void photo(StringBuilder sb){
        // map 자료구조 탐색 && StringBuilder update
        int numOfPerson = owners.size();
        int leftSushi = 0;

        for(Map.Entry<String,List<Sushi>> entry : sushiMap.entrySet()){
            leftSushi += entry.getValue().size();
        }

        sb.append(numOfPerson).append(" ").append(leftSushi).append("\n");
    }
}